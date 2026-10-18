package com.uade.tpo.grupo11.gallery.demo;

import com.uade.tpo.grupo11.gallery.demo.Api.Resp;
import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Datos;
import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Fila;
import com.uade.tpo.grupo11.gallery.demo.Plan.Borrable;
import com.uade.tpo.grupo11.gallery.demo.Plan.FotosDeObra;
import com.uade.tpo.grupo11.gallery.demo.Plan.MarcoAGuardar;
import com.uade.tpo.grupo11.gallery.demo.Plan.VarianteACrear;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Compra;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Encargo;
import com.uade.tpo.grupo11.gallery.demo.Semilla.ItemCompra;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Mensaje;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Obra;
import com.uade.tpo.grupo11.gallery.demo.Semilla.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.uade.tpo.grupo11.gallery.demo.Api.ok;
import static com.uade.tpo.grupo11.gallery.demo.Util.clave;
import static com.uade.tpo.grupo11.gallery.demo.Util.listaIds;
import static com.uade.tpo.grupo11.gallery.demo.Util.lit;

// Hace los cambios del plan: SQL para lo que no necesita pasar por el back y la API para crear (asi valen las reglas
// del back y las fotos se suben a Cloudinary).
//
// Las fotos y los marcos NUNCA se borran con SQL: cuando hay que sacar o cambiar uno se le pide al back (DELETE o PUT), que
// lo destruye en Cloudinary. Asi no quedan archivos huerfanos ni duplicados.
final class Aplicador {

    // Algo que no se pudo borrar por la API: puede haber quedado un archivo huerfano en Cloudinary.
    record Fallida(String tipo, String publicId) {
    }

    private final Api api;
    private final BaseDatos base;
    private final Cache cache;
    private final Map<String, String> tokens = new ConcurrentHashMap<>();
    private final Map<String, Usuario> usuarios = new HashMap<>();
    final List<Fallida> fallidas = new ArrayList<>();

    Aplicador(Api api, BaseDatos base, Cache cache) {
        this.api = api;
        this.base = base;
        this.cache = cache;
        for (Usuario u : Semilla.USUARIOS) usuarios.put(u.email(), u);
    }

    // ---------------------------------------------------------------- sesiones
    private synchronized String tok(String email) {
        String token = tokens.get(email);
        if (token == null) {
            token = entrar(email);
            tokens.put(email, token);
        }
        return token;
    }

    private String entrar(String email) {
        Usuario u = usuarios.get(email);
        Resp r = ok(api.pedir("POST", "/api/v1/auth/authenticate", null, Map.of("email_usuario", email, "contrasenia_usuario", u.clave())),
                "login " + email, 200);
        return r.texto("access_token");
    }

    // El token de un admin con la clave de la demo, o null si no hay (por ejemplo, alguien le cambio la clave).
    private String tokenAdmin() {
        try {
            return tok(Semilla.ADMIN);
        } catch (Falla e) {
            return null;
        }
    }

    // Entra con cada usuario de la semilla que ya existe (en paralelo). Devuelve los emails cuya clave no sirve.
    Set<String> verificarCredenciales(Datos E) {
        Set<String> existentes = E.t("usuario").stream().map(u -> u.s("email_usuario")).collect(Collectors.toSet());
        List<Usuario> aProbar = Semilla.USUARIOS.stream().filter(u -> existentes.contains(u.email())).collect(Collectors.toList());
        Set<String> mal = new HashSet<>();
        List<String[]> resultados = Util.paralelo(5, aProbar, u -> {
            Resp r = api.pedir("POST", "/api/v1/auth/authenticate", null, Map.of("email_usuario", u.email(), "contrasenia_usuario", u.clave()));
            return new String[]{u.email(), r.estado() == 200 ? r.texto("access_token") : null};
        });
        for (String[] r : resultados) {
            if (r[1] != null) tokens.put(r[0], r[1]);
            else mal.add(r[0]);
        }
        return mal;
    }

    // ---------------------------------------------------------------- borrar por la API
    // Devuelve los que no se pudieron borrar. Los de un mismo grupo (las fotos de una obra) se borran de a uno, en orden.
    private List<Borrable> borrarPorApi(String tipo, String ruta, List<Borrable> items, String token) {
        if (items.isEmpty()) return List.of();
        if (token == null) return new ArrayList<>(items);
        Map<Long, List<Borrable>> grupos = new LinkedHashMap<>();
        for (Borrable b : items) grupos.computeIfAbsent(b.grupo(), k -> new ArrayList<>()).add(b);
        List<List<Borrable>> fallos = Util.paralelo(4, new ArrayList<>(grupos.values()), lista -> {
            List<Borrable> fallo = new ArrayList<>();
            for (Borrable b : lista) {
                Resp r = api.pedir("DELETE", ruta + "/" + b.id(), token);
                if (r.estado() != 200 && r.estado() != 204 && r.estado() != 404) {
                    String detalle = String.valueOf(r.cuerpo());
                    System.out.println("    no se pudo borrar " + tipo + " " + b.id() + ": el back respondio " + r.estado() + " "
                            + (detalle.length() > 120 ? detalle.substring(0, 120) : detalle));
                    fallo.add(b);
                }
            }
            return fallo;
        });
        return fallos.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private void anotar(String tipo, List<Borrable> sobran) {
        for (Borrable b : sobran) fallidas.add(new Fallida(tipo, b.publicId()));
    }

    // ---------------------------------------------------------------- aplicar
    void aplicar(Plan p) {
        Map<String, Long> ids = p.ids;

        // 0) las obras de la semilla quedan "vigentes" un rato: el back no deja tocar fotos ni carritos de una eliminada
        //    ni comprar una despublicada. Al final se les pone el estado de la semilla.
        List<Long> vivas = new ArrayList<>();
        for (Map.Entry<String, Long> e : ids.entrySet()) if (e.getKey().startsWith("obra|")) vivas.add(e.getValue());
        List<String> antes = new ArrayList<>();
        if (!vivas.isEmpty()) antes.add("UPDATE obra SET en_venta=1, eliminada=0 WHERE id IN (" + listaIds(vivas) + ")");
        antes.addAll(p.sqlAntesApi);

        // 1) imagenes que sobran: por la API, asi el back las destruye en Cloudinary
        base.sql(antes);
        if (!p.imagenesBorrar.isEmpty()) anotar("imagen", borrarPorApi("imagen", "/api/imagenes", p.imagenesBorrar, tokenAdmin()));

        // 2) borrados y correcciones directas a la base
        base.sql(p.sqlPre);

        // 3) marcos que sobran: ahora que nada los usa se pueden borrar (por la API, que borra su foto)
        if (!p.marcosBorrar.isEmpty()) {
            List<Borrable> sobran = borrarPorApi("marco", "/api/marcos", p.marcosBorrar, tokenAdmin());
            if (!sobran.isEmpty()) {
                base.sql(List.of("DELETE FROM marco WHERE id_marco IN (" + listaIds(sobran.stream().map(Borrable::id).collect(Collectors.toList())) + ")"));
                anotar("marco", sobran);
            }
        }

        // 4) usuarios (en paralelo: el bcrypt de cada registro tarda) y perfiles de artista
        if (!p.usuarios.isEmpty()) {
            List<Object[]> registrados = Util.paralelo(5, p.usuarios, u -> {
                Map<String, Object> cuerpo = new LinkedHashMap<>();
                cuerpo.put("nombre_usuario", u.usuario());
                cuerpo.put("contrasenia_usuario", u.clave());
                cuerpo.put("nombre_persona", u.nombre());
                cuerpo.put("apellido_persona", u.apellido());
                cuerpo.put("email_usuario", u.email());
                cuerpo.put("telefono_usuario", u.telefono());
                if (u.direccion() != null) cuerpo.put("direccion_cliente", u.direccion());
                Resp r = ok(api.pedir("POST", "/api/v1/auth/register", null, cuerpo), "registro " + u.email(), 200);
                String token = r.texto("access_token");
                Resp me = ok(api.pedir("GET", "/api/usuarios/me", token), "me " + u.email(), 200);
                @SuppressWarnings("unchecked")
                Map<String, Object> usuario = (Map<String, Object>) me.get("usuario");
                return new Object[]{u.email(), token, ((Number) usuario.get("id")).longValue()};
            });
            for (Object[] r : registrados) {
                tokens.put((String) r[0], (String) r[1]);
                ids.put(clave("usuario", (String) r[0]), (Long) r[2]);
            }
            List<String> admins = p.usuarios.stream().filter(u -> u.rol().equals("ADMIN")).map(Usuario::email).collect(Collectors.toList());
            if (!admins.isEmpty()) {
                base.sql(admins.stream().map(e -> "UPDATE usuario SET rol_usuario='ADMIN' WHERE email_usuario=" + lit(e)).collect(Collectors.toList()));
                admins.forEach(tokens::remove);
            }
        }
        for (String email : p.perfiles) {
            Usuario u = usuarios.get(email);
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("nombre_artistico", u.perfil().nombreArtistico());
            cuerpo.put("acepta_encargos", u.perfil().aceptaEncargos());
            Resp r = ok(api.pedir("POST", "/api/usuarios/me/perfil-artista", tok(email), cuerpo), "perfil " + email, 201);
            ids.put(clave("perfil", email), r.id());
        }

        // 5) catalogo base
        String admin = tok(Semilla.ADMIN);
        for (String nombre : p.estilos) {
            Resp r = ok(api.pedir("POST", "/api/estilos", admin, Map.of("nombreEstilo", nombre)), "estilo " + nombre, 201);
            ids.put(clave("estilo", nombre), r.id());
        }
        for (Semilla.Tamanio t : p.tamanios) {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("nombre_tamanio", t.nombre());
            cuerpo.put("ancho_lienzo", t.ancho());
            cuerpo.put("largo_lienzo", t.largo());
            ids.put(clave("tamanio", t.nombre()), ok(api.pedir("POST", "/api/tamanios-lienzo", admin, cuerpo), "tamanio " + t.nombre(), 201).id());
        }
        if (!p.marcos.isEmpty()) {
            List<Object[]> guardados = Util.paralelo(4, p.marcos, m -> guardarMarco(m, admin));
            for (Object[] g : guardados) {
                ids.put(clave("marco", (String) g[0]), (Long) g[1]);
                cache.poner("marco:" + g[0], (String) g[2], (String) g[3]);
            }
        }

        // 6) obras y variantes (las variantes arrancan con el stock final mas lo que gastan las compras del historial)
        Map<String, Integer> consumo = Comparador.consumo();
        for (Obra spec : p.obras) {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("nombre_obra", spec.nombre());
            cuerpo.put("descripcion_obra", spec.desc());
            cuerpo.put("en_venta", true);
            cuerpo.put("estilo_ids", spec.estilos().stream().map(n -> ids.get(clave("estilo", n))).collect(Collectors.toList()));
            ids.put(clave("obra", spec.clave()), ok(api.pedir("POST", "/api/obras", tok(spec.artista()), cuerpo), "obra " + spec.nombre(), 201).id());
        }
        for (VarianteACrear vc : p.variantes) {
            Obra spec = vc.obra();
            Semilla.Variante var = vc.variante();
            int inicial = var.stock() + (p.recompra ? consumo.getOrDefault(clave(spec.clave(), var.tam()), 0) : 0);
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("obra_id", ids.get(clave("obra", spec.clave())));
            cuerpo.put("id_tamanio", ids.get(clave("tamanio", var.tam())));
            cuerpo.put("precio_variante", var.precio());
            cuerpo.put("stock_variante", inicial);
            if (var.pct() != null) {
                cuerpo.put("porcentaje_descuento", var.pct());
                cuerpo.put("descuento_hasta", Util.fechaDe(var.hasta()));
            }
            Resp r = ok(api.pedir("POST", "/api/variantes", tok(spec.artista()), cuerpo), "variante " + spec.nombre() + "/" + var.tam(), 201);
            ids.put(clave("variante", spec.clave(), var.tam()), r.id());
        }

        // 7) fotos: una obra por hilo (el orden de subida es el orden de las fotos). Se borran las que tenia por la API y
        //    se suben todas de nuevo: nunca queda una foto repetida ni huerfana.
        for (FotosDeObra f : p.fotos) tok(f.obra().artista());
        if (!p.fotos.isEmpty()) {
            List<Object[]> resultados = Util.paralelo(3, p.fotos, f -> fotosDeUnaObra(f, ids));
            for (Object[] r : resultados) {
                @SuppressWarnings("unchecked")
                List<Borrable> fallo = (List<Borrable>) r[0];
                anotar("imagen", fallo);
                @SuppressWarnings("unchecked")
                List<String[]> subidas = (List<String[]>) r[1];
                for (String[] s : subidas) cache.poner(s[0], s[1], s[2]);
            }
        }

        // 8) compras del historial: se arma el carrito y se hace el checkout (que descuenta el stock y emite una factura por artista)
        if (p.recompra) {
            int n = 0;
            for (Compra c : Semilla.COMPRAS) {
                n++;
                llenarCarrito(c.cliente(), c.items(), ids);
                Resp r = api.pedir("POST", "/api/checkout", tok(c.cliente()), Map.of("tipo_entrega", c.entrega()));
                ok(r, "checkout " + n + " de " + c.cliente(), 201);
            }
        }

        // 9) carritos finales
        for (Map.Entry<String, List<ItemCompra>> e : p.carritos.entrySet()) llenarCarrito(e.getKey(), e.getValue(), ids);

        // 10) encargos: el cliente lo crea, el artista pone precio, el cliente lo acepta... hasta el estado de la semilla
        if (p.reencargos) {
            for (Encargo e : Semilla.ENCARGOS) hacerEncargo(e, ids);
        }

        // 11) estado final de las obras (publicada, despublicada o eliminada)
        List<String> finales = new ArrayList<>();
        for (Obra o : Semilla.OBRAS) {
            Long id = ids.get(clave("obra", o.clave()));
            if (id != null) finales.add("UPDATE obra SET en_venta=" + lit(o.enVenta()) + ", eliminada=" + lit(o.eliminada()) + " WHERE id=" + id);
        }
        base.sql(finales);

        cache.guardar();
    }

    private Object[] guardarMarco(MarcoAGuardar m, String admin) {
        Semilla.Marco marco = m.marco();
        Map<String, Object> campos = new LinkedHashMap<>();
        campos.put("nombre_marco", marco.nombre());
        campos.put("color_marco", marco.color());
        campos.put("precio_marco", marco.precio());
        java.nio.file.Path ruta = Rutas.marco(marco.archivo());
        Resp r = m.existente() == null
                ? ok(api.subir("POST", "/api/marcos", admin, campos, "archivo", ruta, "image/png"), "marco " + marco.nombre(), 201)
                : ok(api.subir("PUT", "/api/marcos/" + m.existente(), admin, campos, "archivo", ruta, "image/png"), "marco " + marco.nombre(), 200);
        return new Object[]{marco.nombre(), r.id(), r.texto("url"), Util.sha1(ruta)};
    }

    // devuelve { fallos al borrar, subidas como {claveCache, url, sha1} }
    private Object[] fotosDeUnaObra(FotosDeObra trabajo, Map<String, Long> ids) {
        Obra spec = trabajo.obra();
        String token = tokens.get(spec.artista());
        List<Borrable> fallo = borrarPorApi("imagen", "/api/imagenes", trabajo.existentes(), token);
        List<String[]> subidas = new ArrayList<>();
        int n = 0;
        for (String archivo : spec.fotos()) {
            n++;
            java.nio.file.Path ruta = Rutas.foto(archivo);
            Resp r = ok(api.subir("POST", "/api/imagenes", token, Map.of("obra_id", ids.get(clave("obra", spec.clave()))), "archivo", ruta, "image/jpeg"),
                    "foto " + archivo + " de " + spec.nombre(), 201);
            subidas.add(new String[]{"foto:" + spec.clave() + ":" + n, r.texto("url"), Util.sha1(ruta)});
        }
        return new Object[]{fallo, subidas};
    }

    private void llenarCarrito(String email, List<ItemCompra> items, Map<String, Long> ids) {
        Resp c = ok(api.pedir("GET", "/api/carritos/me", tok(email)), "carrito de " + email, 200);
        for (ItemCompra it : items) {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("carrito_id", c.id());
            cuerpo.put("variante_id", ids.get(clave("variante", it.obra(), it.tam())));
            cuerpo.put("marco_id", ids.get(clave("marco", it.marco())));
            cuerpo.put("cantidad", it.cant());
            ok(api.pedir("POST", "/api/items-carrito", tok(email), cuerpo), "item " + it.obra() + " de " + email, 201);
        }
    }

    private void hacerEncargo(Encargo e, Map<String, Long> ids) {
        String cliente = tok(e.cliente());
        String artista = tok(e.artista());
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("artista_id", ids.get(clave("perfil", e.artista())));
        cuerpo.put("tamanio_id", ids.get(clave("tamanio", e.tamanio())));
        cuerpo.put("marco_id", ids.get(clave("marco", e.marco())));
        cuerpo.put("tipo_pintura", e.pintura());
        cuerpo.put("tipo_lienzo", e.lienzo());
        cuerpo.put("descripcion_encargo", e.desc());
        long eid = ok(api.pedir("POST", "/api/encargos", cliente, cuerpo), "encargo " + e.desc(), 201).id();
        for (Mensaje m : e.mensajes()) {  // antes de cancelar o pagar: un encargo cerrado ya no admite mensajes
            Map<String, Object> msg = new LinkedHashMap<>();
            msg.put("encargo_id", eid);
            msg.put("contenido", m.texto());
            ok(api.pedir("POST", "/api/mensajes", tok(m.quien()), msg), "mensaje", 201);
        }
        String estado = e.estado();
        Set<String> avanzados = Set.of("APROBADO", "EN_PROCESO", "TERMINADO", "PAGADO");
        if (e.precio() != null) {
            ok(api.pedir("PATCH", "/api/encargos/" + eid + "/precio", artista, Map.of("precio_acordado", e.precio())), "precio del encargo", 200);
        }
        if (avanzados.contains(estado)) ok(api.pedir("POST", "/api/encargos/" + eid + "/aceptar-precio", cliente), "aceptar precio", 200);
        if (Set.of("EN_PROCESO", "TERMINADO", "PAGADO").contains(estado)) {
            ok(api.pedir("PATCH", "/api/encargos/" + eid + "/estado", artista, Map.of("nuevoEstado", "EN_PROCESO")), "encargo EN_PROCESO", 200);
        }
        if (Set.of("TERMINADO", "PAGADO").contains(estado)) {
            ok(api.pedir("PATCH", "/api/encargos/" + eid + "/estado", artista, Map.of("nuevoEstado", "TERMINADO")), "encargo TERMINADO", 200);
        }
        if (estado.equals("PAGADO")) ok(api.pedir("POST", "/api/encargos/" + eid + "/pagar", cliente), "pagar encargo", 200);
        if (estado.equals("CANCELADO")) {
            ok(api.pedir("PATCH", "/api/encargos/" + eid + "/estado", artista, Map.of("nuevoEstado", "CANCELADO")), "cancelar encargo", 200);
        }
    }

    // ---------------------------------------------------------------- --completo
    // Antes de vaciar la base, saca las fotos y los marcos por la API (para que no queden en Cloudinary).
    void vaciarTodo() {
        Datos E = base.estado();
        List<Borrable> imagenes = E.t("imagen").stream()
                .map(i -> new Borrable(i.lp("imagen_id"), i.s("public_id_imagen"), i.lp("obra_id"))).collect(Collectors.toList());
        List<Borrable> marcos = E.t("marco").stream()
                .map(m -> new Borrable(m.lp("id_marco"), m.s("public_id_marco"), m.lp("id_marco"))).collect(Collectors.toList());
        if (!imagenes.isEmpty() || !marcos.isEmpty()) {
            String admin = tokenAdmin();
            if (admin != null) {
                base.sql(List.of("UPDATE obra SET eliminada=0"));  // el back no borra fotos de obras eliminadas
                anotar("imagen", borrarPorApi("imagen", "/api/imagenes", imagenes, admin));
                // los marcos usados por compras o carritos no se pueden borrar: primero se limpia lo que los usa
                base.sql(List.of("DELETE FROM items_factura", "DELETE FROM item_carrito", "DELETE FROM factura", "DELETE FROM compra",
                        "DELETE FROM mensajes", "DELETE FROM encargos"));
                anotar("marco", borrarPorApi("marco", "/api/marcos", marcos, admin));
            } else {
                anotar("imagen", imagenes);
                anotar("marco", marcos);
            }
        }
        base.vaciar();
        tokens.clear();
    }
}
