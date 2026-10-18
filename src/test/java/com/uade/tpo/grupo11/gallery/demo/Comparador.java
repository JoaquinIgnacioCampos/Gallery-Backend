package com.uade.tpo.grupo11.gallery.demo;

import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Datos;
import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Fila;
import com.uade.tpo.grupo11.gallery.demo.Plan.Borrable;
import com.uade.tpo.grupo11.gallery.demo.Plan.FotosDeObra;
import com.uade.tpo.grupo11.gallery.demo.Plan.MarcoAGuardar;
import com.uade.tpo.grupo11.gallery.demo.Plan.VarianteACrear;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Compra;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Encargo;
import com.uade.tpo.grupo11.gallery.demo.Semilla.ItemCompra;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Marco;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Obra;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Tamanio;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Usuario;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Variante;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.uade.tpo.grupo11.gallery.demo.Util.clave;
import static com.uade.tpo.grupo11.gallery.demo.Util.lit;
import static com.uade.tpo.grupo11.gallery.demo.Util.listaIds;
import static com.uade.tpo.grupo11.gallery.demo.Util.numIgual;

// Compara el estado actual de la base con la semilla y arma el plan: que crear, que corregir y que sacar.
// No cambia nada: solo mira.
final class Comparador {

    private Comparador() {
    }

    // sinClave: emails que existen pero cuya clave no es la de la demo.
    static Plan comparar(Datos E, Cache cache, Set<String> sinClave) {
        Plan p = new Plan(E);
        Set<Long> sacados = usuarios(p, sinClave);
        catalogos(p, cache);
        obras(p);
        variantes(p);
        fotos(p, cache);

        boolean cambioLower = !p.obras.isEmpty() || !p.variantes.isEmpty() || p.marcos.stream().anyMatch(m -> m.existente() == null)
                || !p.usuarios.isEmpty() || !p.perfiles.isEmpty() || !sacados.isEmpty();

        compras(p, cambioLower);
        carritos(p, cambioLower, sacados);
        encargos(p, cambioLower);
        stock(p);
        return p;
    }

    private static String sinVacios(String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    private static <T> Map<Long, List<Fila>> agrupar(List<Fila> filas, String columna) {
        Map<Long, List<Fila>> m = new HashMap<>();
        for (Fila f : filas) m.computeIfAbsent(f.l(columna), k -> new ArrayList<>()).add(f);
        return m;
    }

    // ---------------------------------------------------------------- usuarios y perfiles
    private static Set<Long> usuarios(Plan p, Set<String> sinClave) {
        Datos E = p.E;
        Map<String, Long> ids = p.ids;
        Map<String, Fila> porEmail = new HashMap<>();
        for (Fila u : E.t("usuario")) porEmail.put(u.s("email_usuario"), u);
        Map<Long, Fila> perfilDe = new HashMap<>();
        for (Fila a : E.t("artista")) perfilDe.put(a.lp("usuario_id"), a);
        Set<String> emailsSemilla = Semilla.USUARIOS.stream().map(Usuario::email).collect(Collectors.toSet());

        List<Long> aSacar = new ArrayList<>();
        List<String> extras = new ArrayList<>();
        for (Fila u : E.t("usuario")) {
            if (!emailsSemilla.contains(u.s("email_usuario"))) {
                aSacar.add(u.lp("usuario_id"));
                extras.add(u.s("email_usuario"));
            }
        }
        if (!extras.isEmpty()) {
            p.hacer("-", "sacar " + extras.size() + " usuario(s) que no son de la demo (y todo lo suyo): " + String.join(", ", extras));
        }
        for (Usuario u : Semilla.USUARIOS) {
            Fila fila = porEmail.get(u.email());
            if (fila != null && sinClave.contains(u.email())) {
                p.hacer("~", "usuario " + u.email() + ": la clave no es la de la demo, se vuelve a crear");
                aSacar.add(fila.lp("usuario_id"));
                fila = null;
            }
            if (fila == null) {
                p.usuarios.add(u);
                p.hacer("+", "registrar a " + u.email());
                continue;
            }
            ids.put(clave("usuario", u.email()), fila.lp("usuario_id"));
            String rolActual = fila.s("rol_usuario");
            if (!rolActual.equals(u.rol()) && !(u.perfil() != null && rolActual.equals("CLIENTE"))) {
                p.sqlPre.add("UPDATE usuario SET rol_usuario=" + lit(u.rol()) + " WHERE usuario_id=" + fila.lp("usuario_id"));
                p.hacer("~", u.email() + ": rol " + rolActual + " -> " + u.rol());
            } else {
                p.ok("usuario " + u.email());
            }
        }
        Set<Long> sacados = new HashSet<>(aSacar);
        p.quitarUsuarios(aSacar);

        for (Usuario u : Semilla.USUARIOS) {
            Long uid = ids.get(clave("usuario", u.email()));
            Fila a = (uid != null && !sacados.contains(uid)) ? perfilDe.get(uid) : null;
            if (u.perfil() == null) {
                if (a != null) {  // alguien se hizo artista desde la app (boton "¿Querés publicar?"): se deshace
                    p.hacer("-", "perfil de artista de " + u.email() + " (no es de la demo)");
                    p.quitarPerfiles(List.of(a.lp("artista_id")));
                }
                continue;
            }
            if (uid == null || a == null) {
                p.perfiles.add(u.email());
                p.hacer("+", "perfil de artista de " + u.email() + " (" + u.perfil().nombreArtistico() + ")");
                continue;
            }
            ids.put(clave("perfil", u.email()), a.lp("artista_id"));
            if (!Objects.equals(a.s("nombre_artistico"), u.perfil().nombreArtistico()) || a.b("acepta_encargos") != u.perfil().aceptaEncargos()) {
                p.sqlPre.add("UPDATE artista SET nombre_artistico=" + lit(u.perfil().nombreArtistico()) + ", acepta_encargos="
                        + lit(u.perfil().aceptaEncargos()) + " WHERE artista_id=" + a.lp("artista_id"));
                p.hacer("~", "perfil de " + u.email() + ": " + u.perfil().nombreArtistico() + ", acepta encargos " + u.perfil().aceptaEncargos());
            } else {
                p.ok("perfil de artista " + u.perfil().nombreArtistico());
            }
        }

        // direccion de entrega (vive en el carrito, que crea el registro): la de la semilla, o ninguna
        for (Usuario u : Semilla.USUARIOS) {
            Long uid = ids.get(clave("usuario", u.email()));
            if (uid == null || sacados.contains(uid)) continue;
            Fila c = E.t("carrito").stream().filter(x -> uid.equals(x.l("usuario_id"))).findFirst().orElse(null);
            if (c == null) continue;
            if (!Objects.equals(sinVacios(c.s("direccion_cliente")), u.direccion())) {
                p.sqlPre.add("UPDATE carrito SET direccion_cliente=" + lit(u.direccion()) + " WHERE carrito_id=" + c.lp("carrito_id"));
                p.hacer("~", "direccion de " + u.email() + ": " + (u.direccion() == null ? "ninguna" : u.direccion()));
            }
        }
        return sacados;
    }

    // ---------------------------------------------------------------- estilos, tamanios y marcos
    private static Map<String, List<Fila>> porNombre(List<Fila> filas, String columna) {
        Map<String, List<Fila>> m = new LinkedHashMap<>();
        for (Fila f : filas) m.computeIfAbsent(f.s(columna), k -> new ArrayList<>()).add(f);
        return m;
    }

    private static void catalogos(Plan p, Cache cache) {
        Datos E = p.E;
        Map<String, Long> ids = p.ids;
        Map<String, List<Fila>> estilos = porNombre(E.t("estilos"), "nombre_estilo");
        Map<String, List<Fila>> tamanios = porNombre(E.t("tamanio_lienzo"), "nombre_tamanio");
        Map<String, List<Fila>> marcos = porNombre(E.t("marco"), "nombre_marco");

        for (String nombre : Semilla.ESTILOS) {
            List<Fila> filas = estilos.getOrDefault(nombre, List.of());
            if (filas.isEmpty()) {
                p.estilos.add(nombre);
                p.hacer("+", "estilo " + nombre);
                continue;
            }
            ids.put(clave("estilo", nombre), filas.get(0).lp("estilo_id"));
            p.ok("estilo " + nombre);
            if (filas.size() > 1) {
                p.hacer("-", "estilo repetido " + nombre);
                List<Long> extra = filas.subList(1, filas.size()).stream().map(f -> f.lp("estilo_id")).collect(Collectors.toList());
                p.sqlPre.add("DELETE FROM estilo_obra WHERE estilo_id IN (" + listaIds(extra) + ")");
                p.sqlPre.add("DELETE FROM estilo_artista WHERE estilo_id IN (" + listaIds(extra) + ")");
                p.sqlPre.add("DELETE FROM estilos WHERE estilo_id IN (" + listaIds(extra) + ")");
            }
        }
        for (Tamanio t : Semilla.TAMANIOS) {
            List<Fila> filas = tamanios.getOrDefault(t.nombre(), List.of());
            if (filas.isEmpty()) {
                p.tamanios.add(t);
                p.hacer("+", "tamanio " + t.nombre());
                continue;
            }
            Fila f = filas.get(0);
            ids.put(clave("tamanio", t.nombre()), f.lp("id_tamanio"));
            if (!(numIgual(f.get("ancho_lienzo"), t.ancho()) && numIgual(f.get("largo_lienzo"), t.largo()))) {
                p.sqlPre.add("UPDATE tamanio_lienzo SET ancho_lienzo=" + lit(t.ancho()) + ", largo_lienzo=" + lit(t.largo())
                        + " WHERE id_tamanio=" + f.lp("id_tamanio"));
                p.hacer("~", "tamanio " + t.nombre() + ": " + t.ancho() + " x " + t.largo());
            } else {
                p.ok("tamanio " + t.nombre());
            }
            if (filas.size() > 1) {
                p.hacer("-", "tamanio repetido " + t.nombre());
                p.sqlPre.add("DELETE FROM tamanio_lienzo WHERE id_tamanio IN ("
                        + listaIds(filas.subList(1, filas.size()).stream().map(x -> x.lp("id_tamanio")).collect(Collectors.toList())) + ")");
            }
        }
        for (Marco marco : Semilla.MARCOS) {
            List<Fila> filas = marcos.getOrDefault(marco.nombre(), List.of());
            if (filas.isEmpty()) {
                p.marcos.add(new MarcoAGuardar(marco, null));
                p.hacer("+", "marco " + marco.nombre() + " (con su foto)");
                continue;
            }
            Fila f = filas.get(0);
            ids.put(clave("marco", marco.nombre()), f.lp("id_marco"));
            String hash = Util.sha1(Rutas.marco(marco.archivo()));
            boolean fotoOk = cache.coincide("marco:" + marco.nombre(), f.s("url_marco"), hash);
            if (!Objects.equals(f.s("color_marco"), marco.color()) || !numIgual(f.get("precio_marco"), marco.precio()) || !fotoOk) {
                p.marcos.add(new MarcoAGuardar(marco, f.lp("id_marco")));
                p.hacer("~", "marco " + marco.nombre() + ": " + (!fotoOk ? "foto distinta a la de la demo" : "color o precio"));
            } else {
                p.ok("marco " + marco.nombre());
            }
            for (Fila x : filas.subList(1, filas.size())) {
                p.hacer("-", "marco repetido " + marco.nombre());
                p.marcosBorrar.add(new Borrable(x.lp("id_marco"), x.s("public_id_marco"), x.lp("id_marco")));
            }
        }

        // lo que sobra del catalogo (estilos y tamanios por SQL: no tienen archivos; marcos por la API, que borra la foto)
        for (Fila f : E.t("estilos")) {
            if (!Semilla.ESTILOS.contains(f.s("nombre_estilo"))) {
                p.hacer("-", "estilo " + f.s("nombre_estilo") + " (no es de la demo)");
                p.sqlPre.add("DELETE FROM estilo_obra WHERE estilo_id=" + f.lp("estilo_id"));
                p.sqlPre.add("DELETE FROM estilo_artista WHERE estilo_id=" + f.lp("estilo_id"));
                p.sqlPre.add("DELETE FROM estilos WHERE estilo_id=" + f.lp("estilo_id"));
            }
        }
        Set<String> nombresTam = Semilla.TAMANIOS.stream().map(Tamanio::nombre).collect(Collectors.toSet());
        for (Fila f : E.t("tamanio_lienzo")) {
            if (!nombresTam.contains(f.s("nombre_tamanio"))) {
                p.hacer("-", "tamanio " + f.s("nombre_tamanio") + " (no es de la demo)");
                p.sqlPre.add("DELETE FROM tamanio_lienzo WHERE id_tamanio=" + f.lp("id_tamanio"));
            }
        }
        Set<String> nombresMarco = Semilla.MARCOS.stream().map(Marco::nombre).collect(Collectors.toSet());
        for (Fila f : E.t("marco")) {
            if (!nombresMarco.contains(f.s("nombre_marco"))) {
                p.hacer("-", "marco " + f.s("nombre_marco") + " (no es de la demo)");
                p.marcosBorrar.add(new Borrable(f.lp("id_marco"), f.s("public_id_marco"), f.lp("id_marco")));
            }
        }
    }

    // ---------------------------------------------------------------- obras
    private static void obras(Plan p) {
        Datos E = p.E;
        Map<String, Long> ids = p.ids;
        Map<String, List<Fila>> porClave = new HashMap<>();
        for (Fila o : E.t("obra")) porClave.computeIfAbsent(clave(o.s("nombre_obra"), String.valueOf(o.l("artista_id"))), k -> new ArrayList<>()).add(o);
        Map<Long, Set<Long>> estilosDe = new HashMap<>();
        for (Fila r : E.t("estilo_obra")) estilosDe.computeIfAbsent(r.lp("obra_id"), k -> new HashSet<>()).add(r.lp("estilo_id"));

        Set<String> esperadas = new HashSet<>();
        for (Obra spec : Semilla.OBRAS) {
            Long art = ids.get(clave("perfil", spec.artista()));
            List<Fila> filas = art != null ? porClave.getOrDefault(clave(spec.nombre(), String.valueOf(art)), List.of()) : List.of();
            if (art != null) esperadas.add(clave(spec.nombre(), String.valueOf(art)));
            if (filas.isEmpty()) {
                p.obras.add(spec);
                p.hacer("+", "obra " + spec.nombre() + " (" + spec.artista().split("@")[0] + ")");
                continue;
            }
            Fila fila = filas.get(0);
            long oid = fila.lp("id");
            ids.put(clave("obra", spec.clave()), oid);
            if (filas.size() > 1) {
                p.hacer("-", "obra repetida " + spec.nombre());
                p.quitarObras(filas.subList(1, filas.size()).stream().map(x -> x.lp("id")).collect(Collectors.toList()));
            }
            List<String> dif = new ArrayList<>();
            if (!Objects.equals(fila.s("descripcion_obra"), spec.desc())) dif.add("descripcion");
            Set<Long> esperados = new HashSet<>();
            for (String n : spec.estilos()) esperados.add(ids.get(clave("estilo", n)));  // puede haber nulls: se nota como diferencia
            if (!estilosDe.getOrDefault(oid, Set.of()).equals(esperados)) dif.add("estilos");
            if (!dif.isEmpty()) {
                p.sqlPre.add("UPDATE obra SET descripcion_obra=" + lit(spec.desc()) + " WHERE id=" + oid);
                if (dif.contains("estilos") && !esperados.contains(null)) {
                    p.sqlPre.add("DELETE FROM estilo_obra WHERE obra_id=" + oid);
                    for (Long e : esperados) p.sqlPre.add("INSERT INTO estilo_obra (obra_id, estilo_id) VALUES (" + oid + ", " + e + ")");
                }
                p.hacer("~", "obra " + spec.nombre() + ": " + String.join(", ", dif));
            } else {
                p.ok("obra " + spec.nombre());
            }
            if (fila.b("en_venta") != spec.enVenta() || fila.b("eliminada") != spec.eliminada()) {
                String estado = spec.eliminada() ? "eliminada" : (!spec.enVenta() ? "despublicada" : "publicada");
                p.hacer("~", "obra " + spec.nombre() + ": queda " + estado);
            }
        }
        List<Long> sobrantes = new ArrayList<>();
        List<String> nombres = new ArrayList<>();
        for (Fila o : E.t("obra")) {
            if (!esperadas.contains(clave(o.s("nombre_obra"), String.valueOf(o.l("artista_id")))) && !p.obraQuitada(o.lp("id"))) {
                sobrantes.add(o.lp("id"));
                nombres.add(o.s("nombre_obra") + (o.b("eliminada") ? " (eliminada)" : ""));
            }
        }
        if (!sobrantes.isEmpty()) {
            p.hacer("-", "sacar " + sobrantes.size() + " obra(s) que no son de la demo: " + String.join(", ", nombres));
            p.quitarObras(sobrantes);
        }
    }

    // ---------------------------------------------------------------- variantes
    private static void variantes(Plan p) {
        Datos E = p.E;
        Map<String, Long> ids = p.ids;
        Map<String, List<Fila>> porVariante = new HashMap<>();
        for (Fila v : E.t("variante")) porVariante.computeIfAbsent(clave(String.valueOf(v.l("obra_id")), String.valueOf(v.l("id_tamanio"))), k -> new ArrayList<>()).add(v);
        Set<String> esperadas = new HashSet<>();
        for (Obra spec : Semilla.OBRAS) {
            Long oid = ids.get(clave("obra", spec.clave()));
            for (Variante var : spec.variantes()) {
                Long tid = ids.get(clave("tamanio", var.tam()));
                List<Fila> filas = (oid != null && tid != null) ? porVariante.getOrDefault(clave(String.valueOf(oid), String.valueOf(tid)), List.of()) : List.of();
                String etiqueta = spec.nombre() + " / " + var.tam();
                if (filas.isEmpty()) {
                    p.variantes.add(new VarianteACrear(spec, var));
                    p.hacer("+", "variante " + etiqueta);
                    continue;
                }
                esperadas.add(clave(String.valueOf(oid), String.valueOf(tid)));
                Fila f = filas.get(0);
                ids.put(clave("variante", spec.clave(), var.tam()), f.lp("variante_id"));
                if (filas.size() > 1) {
                    p.hacer("-", "variante repetida " + etiqueta);
                    String extra = listaIds(filas.subList(1, filas.size()).stream().map(x -> x.lp("variante_id")).collect(Collectors.toList()));
                    p.sqlPre.add("DELETE FROM item_carrito WHERE variante_id IN (" + extra + ")");
                    p.sqlPre.add("DELETE FROM variante WHERE variante_id IN (" + extra + ")");
                }
                String catEsperada = var.hasta() != null ? var.hasta().tipo() : null;
                List<String> dif = new ArrayList<>();
                if (!numIgual(f.get("precio_variante"), var.precio())) dif.add("precio");
                if (!Objects.equals(f.i("porcentaje_descuento"), var.pct()) || !Objects.equals(Util.categoria(f.s("descuento_hasta")), catEsperada)) dif.add("descuento");
                if (!dif.isEmpty()) {
                    p.sqlPre.add("UPDATE variante SET precio_variante=" + lit(var.precio()) + ", porcentaje_descuento=" + lit(var.pct())
                            + ", descuento_hasta=" + lit(Util.fechaDe(var.hasta())) + " WHERE variante_id=" + f.lp("variante_id"));
                    p.hacer("~", "variante " + etiqueta + ": " + String.join(", ", dif));
                }
            }
        }
        Set<Long> vivas = new HashSet<>();
        for (Map.Entry<String, Long> e : ids.entrySet()) if (e.getKey().startsWith("obra|")) vivas.add(e.getValue());
        List<Long> sobran = new ArrayList<>();
        for (Fila v : E.t("variante")) {
            if (vivas.contains(v.lp("obra_id")) && !esperadas.contains(clave(String.valueOf(v.l("obra_id")), String.valueOf(v.l("id_tamanio"))))
                    && !p.obraQuitada(v.lp("obra_id"))) {
                sobran.add(v.lp("variante_id"));
            }
        }
        if (!sobran.isEmpty()) {
            p.hacer("-", "sacar " + sobran.size() + " variante(s) que no son de la demo");
            p.sqlPre.add("DELETE FROM items_factura WHERE variante_id IN (" + listaIds(sobran) + ")");
            p.sqlPre.add("DELETE FROM item_carrito WHERE variante_id IN (" + listaIds(sobran) + ")");
            p.sqlPre.add("DELETE FROM variante WHERE variante_id IN (" + listaIds(sobran) + ")");
        }
    }

    // ---------------------------------------------------------------- fotos (un archivo = una foto en Cloudinary)
    private static void fotos(Plan p, Cache cache) {
        Datos E = p.E;
        Map<Long, List<Fila>> imagenesDe = agrupar(E.t("imagen"), "obra_id");
        for (Obra spec : Semilla.OBRAS) {
            Long oid = p.ids.get(clave("obra", spec.clave()));
            if (oid == null) {
                if (!spec.fotos().isEmpty()) {
                    p.fotos.add(new FotosDeObra(spec, List.of()));
                    p.hacer("+", spec.fotos().size() + " foto(s) de " + spec.nombre());
                }
                continue;
            }
            List<Fila> existentes = new ArrayList<>(imagenesDe.getOrDefault(oid, List.of()));
            existentes.sort(Comparator.comparingInt(i -> i.i("orden_imagen")));
            boolean bien = existentes.size() == spec.fotos().size();
            for (int n = 0; bien && n < spec.fotos().size(); n++) {
                bien = cache.coincide("foto:" + spec.clave() + ":" + (n + 1), existentes.get(n).s("url_imagen"), Util.sha1(Rutas.foto(spec.fotos().get(n))));
            }
            if (bien) {
                p.ok("fotos de " + spec.nombre() + " (" + spec.fotos().size() + ")");
            } else {
                List<Borrable> aBorrar = existentes.stream().map(i -> new Borrable(i.lp("imagen_id"), i.s("public_id_imagen"), oid)).collect(Collectors.toList());
                p.fotos.add(new FotosDeObra(spec, aBorrar));
                p.hacer("~", "fotos de " + spec.nombre() + ": " + existentes.size() + " en la base, " + spec.fotos().size() + " en la semilla (se rehacen)");
            }
        }
    }

    // ---------------------------------------------------------------- compras del historial (huella de cada una)
    private static void compras(Plan p, boolean cambioLower) {
        Datos E = p.E;
        Map<String, Long> ids = p.ids;
        Map<Long, List<String>> itemsDe = new HashMap<>();
        for (Fila it : E.t("items_factura")) {
            itemsDe.computeIfAbsent(it.l("factura_id"), k -> new ArrayList<>()).add(it.l("variante_id") + "," + it.l("marco_id") + "," + it.l("cantidad_items"));
        }
        Map<Long, List<Long>> facturasDe = new HashMap<>();
        for (Fila f : E.t("factura")) facturasDe.computeIfAbsent(f.l("compra_id"), k -> new ArrayList<>()).add(f.lp("factura_id"));
        Map<String, Integer> reales = new HashMap<>();
        for (Fila c : E.t("compra")) {
            List<String> items = new ArrayList<>();
            for (Long fid : facturasDe.getOrDefault(c.l("compra_id"), List.of())) items.addAll(itemsDe.getOrDefault(fid, List.of()));
            if (!items.isEmpty()) {  // una compra sin items es la factura de un encargo
                Collections.sort(items);
                reales.merge(c.l("usuario_id") + "|" + c.s("tipo_entrega") + "|" + String.join(";", items), 1, Integer::sum);
            }
        }
        Map<String, Integer> esperadas = new HashMap<>();
        boolean completas = true;
        for (Compra c : Semilla.COMPRAS) {
            List<String> items = new ArrayList<>();
            for (ItemCompra it : c.items()) {
                Long vid = ids.get(clave("variante", it.obra(), it.tam()));
                Long mid = ids.get(clave("marco", it.marco()));
                completas = completas && vid != null && mid != null;
                items.add(vid + "," + mid + "," + it.cant());
            }
            Collections.sort(items);
            esperadas.merge(ids.get(clave("usuario", c.cliente())) + "|" + c.entrega() + "|" + String.join(";", items), 1, Integer::sum);
        }
        if (!cambioLower && completas && reales.equals(esperadas)) {
            p.ok("compras del historial (" + Semilla.COMPRAS.size() + ")");
        } else {
            p.recompra = true;
            if (!E.t("compra").isEmpty() || !E.t("factura").isEmpty() || !E.t("items_factura").isEmpty()) {
                p.sqlPre.add("DELETE FROM items_factura");
                p.sqlPre.add("DELETE FROM factura");
                p.sqlPre.add("DELETE FROM compra");
            }
            p.hacer("~", "compras del historial (" + Semilla.COMPRAS.size() + "), con sus facturas");
        }
    }

    // ---------------------------------------------------------------- carritos
    private static void carritos(Plan p, boolean cambioLower, Set<Long> sacados) {
        Datos E = p.E;
        Map<String, Long> ids = p.ids;
        Map<Long, Fila> carritoDe = new HashMap<>();
        for (Fila c : E.t("carrito")) carritoDe.put(c.lp("usuario_id"), c);
        Map<Long, List<Fila>> itemsCarrito = agrupar(E.t("item_carrito"), "carrito_id");
        for (Map.Entry<String, List<ItemCompra>> e : Semilla.CARRITOS.entrySet()) {
            String email = e.getKey();
            List<ItemCompra> deseados = e.getValue();
            Long uid = ids.get(clave("usuario", email));
            Fila c = (uid != null && !sacados.contains(uid)) ? carritoDe.get(uid) : null;
            List<Fila> actuales = c != null ? itemsCarrito.getOrDefault(c.lp("carrito_id"), List.of()) : List.of();
            List<String> esperados = new ArrayList<>();
            for (ItemCompra it : deseados) {
                esperados.add(ids.get(clave("variante", it.obra(), it.tam())) + "," + ids.get(clave("marco", it.marco())) + "," + it.cant());
            }
            List<String> reales = new ArrayList<>();
            for (Fila i : actuales) reales.add(i.l("variante_id") + "," + i.l("marco_id") + "," + i.l("cantidad"));
            Collections.sort(esperados);
            Collections.sort(reales);
            if (c != null && !cambioLower && !p.recompra && reales.equals(esperados)) {
                p.ok("carrito de " + email + " (" + deseados.size() + " item)");
                continue;
            }
            if (c != null && !actuales.isEmpty()) {
                p.sqlPre.add("DELETE FROM item_carrito WHERE carrito_id=" + c.lp("carrito_id"));
            }
            if (!deseados.isEmpty()) {
                p.carritos.put(email, deseados);
                p.hacer("~", "carrito de " + email + " (" + deseados.size() + " item)");
            } else if (!actuales.isEmpty()) {
                p.hacer("~", "vaciar el carrito de " + email);
            } else {
                p.ok("carrito de " + email + " (vacio)");
            }
        }
    }

    // ---------------------------------------------------------------- encargos y mensajes
    private static void encargos(Plan p, boolean cambioLower) {
        Datos E = p.E;
        Map<String, Long> ids = p.ids;
        boolean okEnc = !cambioLower && !p.recompra && E.t("encargos").size() == Semilla.ENCARGOS.size();
        if (okEnc) {
            List<Fila> libres = new ArrayList<>(E.t("encargos"));
            Map<Long, List<Fila>> mensajesDe = agrupar(E.t("mensajes"), "encargo_id");
            for (Encargo e : Semilla.ENCARGOS) {
                Long clienteId = ids.get(clave("usuario", e.cliente()));
                Fila fila = libres.stream()
                        .filter(x -> e.desc().equals(x.s("descripcion_encargo")) && Objects.equals(x.l("usuario_id"), clienteId))
                        .findFirst().orElse(null);
                if (fila == null) {
                    okEnc = false;
                    break;
                }
                libres.remove(fila);
                List<Fila> msgs = new ArrayList<>(mensajesDe.getOrDefault(fila.l("encargo_id"), List.of()));
                msgs.sort(Comparator.comparingLong(m -> m.lp("mensaje_id")));
                List<String> esperadosM = e.mensajes().stream().map(m -> ids.get(clave("usuario", m.quien())) + "|" + m.texto()).collect(Collectors.toList());
                List<String> realesM = msgs.stream().map(m -> m.l("usuario_emisor") + "|" + m.s("contenido_mensaje")).collect(Collectors.toList());
                okEnc = Objects.equals(fila.l("artista_id"), ids.get(clave("perfil", e.artista())))
                        && Objects.equals(fila.l("tamanio_id"), ids.get(clave("tamanio", e.tamanio())))
                        && Objects.equals(fila.l("marco_id"), ids.get(clave("marco", e.marco())))
                        && Objects.equals(fila.s("tipo_pintura"), e.pintura()) && Objects.equals(fila.s("tipo_lienzo"), e.lienzo())
                        && Objects.equals(fila.s("estado_encargo"), e.estado()) && numIgual(fila.get("precio_acordado"), e.precio())
                        && realesM.equals(esperadosM);
                if (!okEnc) break;
            }
            int totalMensajes = Semilla.ENCARGOS.stream().mapToInt(x -> x.mensajes().size()).sum();
            if (okEnc && E.t("mensajes").size() != totalMensajes) okEnc = false;
        }
        if (okEnc) {
            p.ok("encargos (" + Semilla.ENCARGOS.size() + ") con sus mensajes");
            return;
        }
        p.reencargos = true;
        if (!E.t("encargos").isEmpty() || !E.t("mensajes").isEmpty()) {
            p.sqlPre.add("DELETE FROM mensajes");
            p.sqlPre.add("DELETE FROM encargos");
            // la factura de un encargo terminado es una compra sin items: se va con el encargo
            List<Long> facturas = E.t("encargos").stream().map(x -> x.l("factura_id")).filter(Objects::nonNull).collect(Collectors.toList());
            if (!facturas.isEmpty() && !p.recompra) {
                List<Long> compras = E.t("factura").stream().filter(f -> facturas.contains(f.lp("factura_id"))).map(f -> f.lp("compra_id")).collect(Collectors.toList());
                p.sqlPre.add("DELETE FROM factura WHERE factura_id IN (" + listaIds(facturas) + ")");
                if (!compras.isEmpty()) p.sqlPre.add("DELETE FROM compra WHERE compra_id IN (" + listaIds(compras) + ")");
            }
        }
        p.hacer("~", "encargos (" + Semilla.ENCARGOS.size() + ") y mensajes");
    }

    // ---------------------------------------------------------------- stock
    // El de la semilla es el FINAL; si se rehacen las compras, las variantes arrancan con ese mas lo que las compras gastan.
    private static void stock(Plan p) {
        Datos E = p.E;
        Map<String, Integer> consumo = consumo();
        Map<Long, Fila> variantes = new HashMap<>();
        for (Fila v : E.t("variante")) variantes.put(v.lp("variante_id"), v);
        for (Obra spec : Semilla.OBRAS) {
            for (Variante var : spec.variantes()) {
                Long vid = p.ids.get(clave("variante", spec.clave(), var.tam()));
                if (vid == null) continue;
                int esperado = var.stock() + (p.recompra ? consumo.getOrDefault(clave(spec.clave(), var.tam()), 0) : 0);
                Integer actual = variantes.get(vid).i("stock_variante");
                if (actual == null || actual != esperado) {
                    p.sqlPre.add("UPDATE variante SET stock_variante=" + esperado + " WHERE variante_id=" + vid);
                    p.hacer("~", "stock de " + spec.nombre() + " / " + var.tam() + ": " + esperado);
                }
            }
        }
    }

    // Unidades que gastan las compras del historial, por (obra, tamanio).
    static Map<String, Integer> consumo() {
        Map<String, Integer> m = new HashMap<>();
        for (Compra c : Semilla.COMPRAS) {
            for (ItemCompra it : c.items()) m.merge(clave(it.obra(), it.tam()), it.cant(), Integer::sum);
        }
        return m;
    }
}
