package com.uade.tpo.grupo11.gallery.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

// Lo que ya se subio a Cloudinary: para cada foto o marco de la semilla, su URL y el hash del archivo que se subio.
// Sirve para saber si lo que hay en la base sigue siendo el archivo actual de la semilla (sin esto habria que
// volver a subir todo en cada corrida). Esta atada a la base de datos: la cache de otra base no sirve.
// Es local (demo/.cache) y no se versiona.
final class Cache {

    private final Map<String, Object> datos;

    private Cache(Map<String, Object> datos) {
        this.datos = datos;
    }

    @SuppressWarnings("unchecked")
    static Cache cargar(String destino) {
        try {
            if (Files.exists(Rutas.cache())) {
                Object leido = Json.leer(Files.readString(Rutas.cache(), StandardCharsets.UTF_8));
                if (leido instanceof Map && destino.equals(((Map<String, Object>) leido).get("_destino"))) {
                    return new Cache((Map<String, Object>) leido);
                }
            }
        } catch (IOException | RuntimeException ignorada) {
            // una cache ilegible es como no tener cache: se vuelve a armar
        }
        return vacia(destino);
    }

    static Cache vacia(String destino) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("_destino", destino);
        return new Cache(m);
    }

    // Una entrada es { url, sha1 }; null si no hay.
    @SuppressWarnings("unchecked")
    Map<String, Object> entrada(String clave) {
        Object o = datos.get(clave);
        return o instanceof Map ? (Map<String, Object>) o : null;
    }

    // true si hay una entrada para esa clave con esa URL y ese hash.
    boolean coincide(String clave, String url, String sha1) {
        Map<String, Object> e = entrada(clave);
        return e != null && url != null && !url.isEmpty() && url.equals(e.get("url")) && sha1.equals(e.get("sha1"));
    }

    // Deja la cache vacia (para la misma base).
    void limpiar() {
        Object destino = datos.get("_destino");
        datos.clear();
        datos.put("_destino", destino);
    }

    void poner(String clave, String url, String sha1) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("url", url);
        e.put("sha1", sha1);
        datos.put(clave, e);
    }

    void guardar() {
        try {
            Files.createDirectories(Rutas.cacheDir());
            Files.writeString(Rutas.cache(), Json.escribir(datos), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new Falla("No se pudo guardar la cache en " + Rutas.cache() + ": " + e.getMessage());
        }
    }
}
