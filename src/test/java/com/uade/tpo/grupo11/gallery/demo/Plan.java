package com.uade.tpo.grupo11.gallery.demo;

import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Datos;
import com.uade.tpo.grupo11.gallery.demo.BaseDatos.Fila;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// El plan: todo lo que hay que cambiar para llegar a la semilla, y por que.
final class Plan {

    record Linea(String simbolo, String texto) {
    }

    // Algo que hay que borrar por la API. grupo: los de un mismo grupo (las fotos de una obra) se borran de a uno,
    // en orden, porque el back reordena las que quedan y dos borrados a la vez chocan.
    record Borrable(long id, String publicId, long grupo) {
    }

    record MarcoAGuardar(Semilla.Marco marco, Long existente) {
    }

    record VarianteACrear(Semilla.Obra obra, Semilla.Variante variante) {
    }

    record FotosDeObra(Semilla.Obra obra, List<Borrable> existentes) {
    }

    final Datos E;
    final Map<String, Long> ids = new LinkedHashMap<>();   // claves naturales -> id actual en la base
    final List<Linea> lineas = new ArrayList<>();
    int cambios = 0;
    int iguales = 0;

    // correcciones directas a la base
    final List<String> sqlAntesApi = new ArrayList<>();    // van antes de borrar imagenes por la API (dejan las obras "vigentes")
    final List<String> sqlPre = new ArrayList<>();         // borrados y correcciones, antes de usar la API para crear

    // imagenes que hay que borrar POR LA API (asi el back las destruye en Cloudinary)
    final List<Borrable> imagenesBorrar = new ArrayList<>();
    final List<Borrable> marcosBorrar = new ArrayList<>();  // despues de limpiar lo que los usa

    private final Set<Long> obrasQuitadas = new HashSet<>();
    private final Set<Long> perfilesQuitados = new HashSet<>();
    private final Set<Long> usuariosQuitados = new HashSet<>();

    // lo que hay que crear
    final List<Semilla.Usuario> usuarios = new ArrayList<>();
    final List<String> perfiles = new ArrayList<>();          // emails que necesitan perfil de artista
    final List<String> estilos = new ArrayList<>();
    final List<Semilla.Tamanio> tamanios = new ArrayList<>();
    final List<MarcoAGuardar> marcos = new ArrayList<>();     // crear o reemplazar su foto
    final List<Semilla.Obra> obras = new ArrayList<>();
    final List<VarianteACrear> variantes = new ArrayList<>();
    final List<FotosDeObra> fotos = new ArrayList<>();        // borrar las que tiene y subir todas de nuevo
    final Map<String, List<Semilla.ItemCompra>> carritos = new LinkedHashMap<>();  // email -> carrito final
    boolean recompra = false;
    boolean reencargos = false;

    Plan(Datos E) {
        this.E = E;
    }

    void ok(String texto) {
        iguales++;
        lineas.add(new Linea("=", texto));
    }

    void hacer(String simbolo, String texto) {
        lineas.add(new Linea(simbolo, texto));
        cambios++;
    }

    boolean obraQuitada(long id) {
        return obrasQuitadas.contains(id);
    }

    // ---------------------------------------------------------------- sacar cosas y todo lo que depende de ellas
    void quitarObras(Collection<Long> obraIds) {
        List<Long> nuevas = obraIds.stream().filter(o -> !obrasQuitadas.contains(o)).distinct().collect(Collectors.toList());
        if (nuevas.isEmpty()) return;
        obrasQuitadas.addAll(nuevas);
        for (Fila im : E.t("imagen")) {
            if (nuevas.contains(im.lp("obra_id"))) {
                imagenesBorrar.add(new Borrable(im.lp("imagen_id"), im.s("public_id_imagen"), im.lp("obra_id")));
            }
        }
        // el back no deja borrar fotos de una obra eliminada: se la "reabre" un instante
        sqlAntesApi.add("UPDATE obra SET eliminada=0 WHERE id IN (" + Util.listaIds(nuevas) + ")");
        List<Long> vids = E.t("variante").stream().filter(v -> nuevas.contains(v.lp("obra_id"))).map(v -> v.lp("variante_id")).collect(Collectors.toList());
        if (!vids.isEmpty()) {
            sqlPre.add("DELETE FROM items_factura WHERE variante_id IN (" + Util.listaIds(vids) + ")");
            sqlPre.add("DELETE FROM item_carrito WHERE variante_id IN (" + Util.listaIds(vids) + ")");
            sqlPre.add("DELETE FROM variante WHERE variante_id IN (" + Util.listaIds(vids) + ")");
        }
        sqlPre.add("DELETE FROM imagen WHERE obra_id IN (" + Util.listaIds(nuevas) + ")");
        sqlPre.add("DELETE FROM estilo_obra WHERE obra_id IN (" + Util.listaIds(nuevas) + ")");
        sqlPre.add("DELETE FROM obra WHERE id IN (" + Util.listaIds(nuevas) + ")");
    }

    void quitarPerfiles(Collection<Long> perfilIds) {
        List<Long> nuevos = perfilIds.stream().filter(a -> !perfilesQuitados.contains(a)).distinct().collect(Collectors.toList());
        if (nuevos.isEmpty()) return;
        perfilesQuitados.addAll(nuevos);
        quitarObras(E.t("obra").stream().filter(o -> nuevos.contains(o.lp("artista_id"))).map(o -> o.lp("id")).collect(Collectors.toList()));
        List<Long> facturas = E.t("factura").stream().filter(f -> nuevos.contains(f.lp("artista_id"))).map(f -> f.lp("factura_id")).collect(Collectors.toList());
        List<Long> encargos = E.t("encargos").stream().filter(e -> nuevos.contains(e.lp("artista_id"))).map(e -> e.lp("encargo_id")).collect(Collectors.toList());
        if (!facturas.isEmpty()) {
            sqlPre.add("DELETE FROM items_factura WHERE factura_id IN (" + Util.listaIds(facturas) + ")");
            sqlPre.add("DELETE FROM factura WHERE factura_id IN (" + Util.listaIds(facturas) + ")");
        }
        if (!encargos.isEmpty()) {
            sqlPre.add("DELETE FROM mensajes WHERE encargo_id IN (" + Util.listaIds(encargos) + ")");
            sqlPre.add("DELETE FROM encargos WHERE encargo_id IN (" + Util.listaIds(encargos) + ")");
        }
        sqlPre.add("DELETE FROM estilo_artista WHERE artista_id IN (" + Util.listaIds(nuevos) + ")");
        sqlPre.add("DELETE FROM artista WHERE artista_id IN (" + Util.listaIds(nuevos) + ")");
    }

    void quitarUsuarios(Collection<Long> usuarioIds) {
        List<Long> nuevos = usuarioIds.stream().filter(u -> !usuariosQuitados.contains(u)).distinct().collect(Collectors.toList());
        if (nuevos.isEmpty()) return;
        usuariosQuitados.addAll(nuevos);
        quitarPerfiles(E.t("artista").stream().filter(a -> nuevos.contains(a.lp("usuario_id"))).map(a -> a.lp("artista_id")).collect(Collectors.toList()));
        List<Long> carritos = E.t("carrito").stream().filter(c -> nuevos.contains(c.lp("usuario_id"))).map(c -> c.lp("carrito_id")).collect(Collectors.toList());
        List<Long> compras = E.t("compra").stream().filter(c -> nuevos.contains(c.lp("usuario_id"))).map(c -> c.lp("compra_id")).collect(Collectors.toList());
        List<Long> facturas = E.t("factura").stream().filter(f -> compras.contains(f.lp("compra_id"))).map(f -> f.lp("factura_id")).collect(Collectors.toList());
        List<Long> encargos = E.t("encargos").stream().filter(e -> nuevos.contains(e.lp("usuario_id"))).map(e -> e.lp("encargo_id")).collect(Collectors.toList());
        if (!facturas.isEmpty()) {
            sqlPre.add("DELETE FROM items_factura WHERE factura_id IN (" + Util.listaIds(facturas) + ")");
            sqlPre.add("DELETE FROM factura WHERE factura_id IN (" + Util.listaIds(facturas) + ")");
        }
        if (!compras.isEmpty()) sqlPre.add("DELETE FROM compra WHERE compra_id IN (" + Util.listaIds(compras) + ")");
        if (!encargos.isEmpty()) {
            sqlPre.add("DELETE FROM mensajes WHERE encargo_id IN (" + Util.listaIds(encargos) + ")");
            sqlPre.add("DELETE FROM encargos WHERE encargo_id IN (" + Util.listaIds(encargos) + ")");
        }
        sqlPre.add("DELETE FROM mensajes WHERE usuario_emisor IN (" + Util.listaIds(nuevos) + ")");
        if (!carritos.isEmpty()) {
            sqlPre.add("DELETE FROM item_carrito WHERE carrito_id IN (" + Util.listaIds(carritos) + ")");
            sqlPre.add("DELETE FROM carrito WHERE carrito_id IN (" + Util.listaIds(carritos) + ")");
        }
        sqlPre.add("DELETE FROM usuario WHERE usuario_id IN (" + Util.listaIds(nuevos) + ")");
    }
}
