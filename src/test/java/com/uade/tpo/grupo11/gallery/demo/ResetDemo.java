package com.uade.tpo.grupo11.gallery.demo;

import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Datos;
import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Fila;
import com.uade.tpo.grupo11.gallery.demo.Plan.Linea;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Deja la base en el estado de la demo (los datos estan en Semilla) cambiando SOLO lo que hace falta.
 *
 * Lee el estado actual de la base, lo compara con la semilla y corrige unicamente las diferencias: lo que ya esta bien no
 * se toca (no se vuelven a registrar usuarios, ni a crear el catalogo, ni a subir fotos). Lo que sobra (obras de prueba,
 * usuarios nuevos, compras sueltas...) se saca.
 *
 * Cloudinary: las fotos y los marcos NUNCA se borran con SQL. Cuando hay que sacar o cambiar una imagen se pide al back
 * (DELETE o PUT), que la destruye en Cloudinary, asi que no quedan archivos huerfanos ni duplicados. Si en algun caso no
 * se pudo (por ejemplo, no hay un admin con la clave de la demo), el script lo avisa y deja la lista en
 * demo/.cache/huerfanas-cloudinary.txt.
 *
 * Como correrlo (con el back levantado): desde IntelliJ, con el triangulo verde de esta clase (los argumentos se
 * ponen en Edit Configurations > Program arguments), o por consola (ver demo/README.md).
 *
 * Argumentos:
 *   (ninguno)    corrige las diferencias (pide confirmacion si va a borrar algo)
 *   --ver        solo muestra las diferencias, no cambia nada
 *   --completo   vacia la base y arma todo desde cero
 *   --si         no pide confirmacion
 *   -v           muestra tambien lo que ya estaba bien
 *
 * Variables de entorno (opcionales):
 *   BASE         direccion del back (por defecto http://localhost:{server.port o 8080})
 *   GALLERY_DB   nombre de la base (para probar contra una copia descartable); la conexion sale del application.properties
 */
public final class ResetDemo {

    private ResetDemo() {
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
        int codigo;
        try {
            codigo = ejecutar(args);
        } catch (Falla e) {
            System.out.println("\nFALLA: " + e.getMessage());
            codigo = 2;
        }
        System.exit(codigo);
    }

    private static int ejecutar(String[] args) {
        boolean ver = false, completo = false, si = false, detalle = false;
        for (String a : args) {
            switch (a) {
                case "--ver": ver = true; break;
                case "--completo": completo = true; break;
                case "--si": si = true; break;
                case "-v": case "--detalle": detalle = true; break;
                default:
                    System.out.println("Argumento desconocido: " + a);
                    System.out.println("Argumentos: --ver, --completo, --si, -v");
                    return 1;
            }
        }
        long inicio = System.currentTimeMillis();
        Path proyecto = Rutas.proyecto();

        String direccion = System.getenv("BASE") != null && !System.getenv("BASE").isBlank()
                ? System.getenv("BASE") : "http://localhost:" + puertoDelBack(proyecto);
        System.out.println("== Chequeando que la app este arriba en " + direccion + " ==");
        Api api = new Api(direccion);
        int estado;
        try {
            estado = api.pedir("GET", "/v3/api-docs", null).estado();
        } catch (Falla e) {
            estado = 0;
        }
        if (estado != 200) {
            System.out.println("No responde " + direccion + ". Arranca el back (desde el IDE o con mvnw spring-boot:run) y volve a correr esto.");
            return 1;
        }

        try (BaseDatos base = new BaseDatos(proyecto)) {
            Datos E;
            try {
                E = base.estado();
            } catch (Falla e) {
                System.out.println("\n" + e.getMessage());
                return 1;
            }
            String destino = E.destino();
            Cache cache = Cache.cargar(destino);
            Aplicador aplicador = new Aplicador(api, base, cache);

            System.out.println("== Base de datos: " + destino + " ==");
            if (completo) {
                if (ver) {
                    E = vacia(destino);
                    cache.limpiar();
                } else {
                    if (!confirmar(destino, null, true, si)) {
                        System.out.println("Cancelado.");
                        return 1;
                    }
                    System.out.println("== Vaciando la base (las fotos y los marcos se sacan antes por el back) ==");
                    aplicador.vaciarTodo();
                    E = base.estado();
                    cache.limpiar();
                }
            }

            Set<String> sinClave = aplicador.verificarCredenciales(E);
            Plan p = Comparador.comparar(E, cache, sinClave);

            System.out.println("\n== Estado actual contra la semilla ==");
            mostrar(p, detalle);
            if (p.cambios == 0) {
                informarHuerfanas(aplicador.fallidas);
                System.out.printf("%nLa base ya esta en el estado de la demo: no hay nada que cambiar. (%.1f s)%n", segundos(inicio));
                return 0;
            }
            if (ver) {
                System.out.println("\n" + p.cambios + " diferencia(s). Con --ver no se cambia nada.");
                return 0;
            }
            if (!completo && !confirmar(destino, p, false, si)) {
                System.out.println("Cancelado.");
                return 1;
            }

            System.out.println("\n== Aplicando " + p.cambios + " correccion(es) ==");
            try {
                aplicador.aplicar(p);
            } catch (Falla e) {
                informarHuerfanas(aplicador.fallidas);
                System.out.println("\nFALLA: " + e.getMessage());
                System.out.println("Se puede volver a correr: solo se corrige lo que falte.");
                return 2;
            }
            informarHuerfanas(aplicador.fallidas);

            // Verificacion final: se vuelve a leer la base y se compara con la semilla
            Datos E2 = base.estado();
            Plan p2 = Comparador.comparar(E2, cache, aplicador.verificarCredenciales(E2));
            if (p2.cambios != 0) {
                System.out.println("\nATENCION: despues de aplicar quedaron diferencias:");
                mostrar(p2, false);
                return 3;
            }
            System.out.printf("%nVerificado: la base coincide con la semilla. (%.1f s)%n", segundos(inicio));
            mostrarCuentas();
            return 0;
        }
    }

    private static double segundos(long inicio) {
        return (System.currentTimeMillis() - inicio) / 1000.0;
    }

    // Solo se lee server.port (nada mas) del application.properties.
    private static String puertoDelBack(Path proyecto) {
        Path archivo = proyecto.resolve("src/main/resources/application.properties");
        try {
            if (Files.exists(archivo)) {
                Pattern patron = Pattern.compile("\\s*server\\.port\\s*[=:]\\s*(\\d+).*");
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    Matcher m = patron.matcher(linea);
                    if (m.matches()) return m.group(1);
                }
            }
        } catch (IOException ignorada) {
            // se usa el puerto de siempre
        }
        return "8080";
    }

    private static Datos vacia(String destino) {
        Map<String, List<Fila>> tablas = new LinkedHashMap<>();
        for (String t : BaseDatos.TABLAS) tablas.put(t, new ArrayList<>());
        return new Datos(destino, tablas);
    }

    private static void mostrar(Plan p, boolean detalle) {
        Map<String, String> secciones = new LinkedHashMap<>();
        secciones.put("+", "Crear");
        secciones.put("~", "Corregir");
        secciones.put("-", "Sacar");
        for (Map.Entry<String, String> s : secciones.entrySet()) {
            List<Linea> lineas = p.lineas.stream().filter(l -> l.simbolo().equals(s.getKey())).collect(Collectors.toList());
            if (lineas.isEmpty()) continue;
            System.out.println("  " + s.getValue() + " (" + lineas.size() + ")");
            for (Linea l : lineas) System.out.println("    " + s.getKey() + " " + l.texto());
        }
        if (detalle) {
            for (Linea l : p.lineas) if (l.simbolo().equals("=")) System.out.println("    = " + l.texto());
        }
        if (p.iguales > 0) {
            System.out.println("  Ya estaba bien: " + p.iguales + " elemento(s)" + (detalle ? "" : " (con -v se listan)"));
        }
    }

    private static boolean confirmar(String destino, Plan p, boolean completo, boolean si) {
        boolean quitan = p != null && p.lineas.stream().anyMatch(l -> l.simbolo().equals("-"));
        if (si || !(quitan || completo)) return true;
        System.out.println("\nBase de datos: " + destino);
        if (completo) System.out.println("Con --completo se VACIAN todas las tablas de esa base.");
        else System.out.println("Se va a borrar de esa base lo que no es de la demo (ver 'Sacar' arriba).");
        System.out.print("¿Continuar? [s/N] ");
        System.out.flush();
        String respuesta;
        try {
            respuesta = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            respuesta = null;
        }
        if (respuesta == null) {
            System.out.println("\nSin terminal para confirmar: volve a correrlo con --si si es lo que querés.");
            return false;
        }
        String r = respuesta.trim().toLowerCase();
        return r.equals("s") || r.equals("si") || r.equals("sí") || r.equals("y") || r.equals("yes");
    }

    private static void informarHuerfanas(List<Aplicador.Fallida> fallidas) {
        if (fallidas.isEmpty()) return;
        Set<String> publicos = fallidas.stream().map(Aplicador.Fallida::publicId).filter(x -> x != null && !x.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        try {
            Files.createDirectories(Rutas.cacheDir());
            Files.write(Rutas.huerfanas(), (String.join("\n", publicos) + "\n").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("No se pudo escribir " + Rutas.huerfanas() + ": " + e.getMessage());
        }
        System.out.println("\nATENCION: " + fallidas.size() + " imagen(es) no se pudieron borrar por el back (hace falta un admin con la clave de la demo).");
        System.out.println("Pueden haber quedado archivos huerfanos en Cloudinary; sus public_id estan en " + Rutas.huerfanas());
        fallidas.clear();
    }

    private static void mostrarCuentas() {
        System.out.println("\nCuentas (la clave es la misma para cada tipo):");
        String[][] grupos = {{"ADMIN", "Admin"}, {"ARTISTA_CLIENTE", "Artistas"}, {"CLIENTE", "Clientes"}};
        for (String[] g : grupos) {
            List<Semilla.Usuario> grupo = Semilla.USUARIOS.stream().filter(u -> u.rol().equals(g[0])).collect(Collectors.toList());
            if (grupo.isEmpty()) continue;
            System.out.printf("  %-9s %s   /  %s%n", g[1], grupo.stream().map(u -> u.email().split("@")[0]).collect(Collectors.joining(", ")), grupo.get(0).clave());
        }
        System.out.println("  (los emails terminan en @gallery.com; en demo/README.md esta que probar con cada uno)");
    }
}
