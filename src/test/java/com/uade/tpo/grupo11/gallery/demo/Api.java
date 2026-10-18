package com.uade.tpo.grupo11.gallery.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

// Habla con el back por HTTP. Un solo cliente para todos los hilos (mantiene las conexiones abiertas).
final class Api {

    // La respuesta: el codigo y el cuerpo ya leido como JSON (o como texto si no lo es).
    record Resp(int estado, Object cuerpo) {

        @SuppressWarnings("unchecked")
        Map<String, Object> mapa() {
            return cuerpo instanceof Map ? (Map<String, Object>) cuerpo : Map.of();
        }

        Object get(String clave) {
            return mapa().get(clave);
        }

        long id() {
            Object id = get("id");
            if (id == null) throw new Falla("La respuesta del back no trae id: " + cuerpo);
            return ((Number) id).longValue();
        }

        String texto(String clave) {
            Object v = get(clave);
            return v == null ? null : v.toString();
        }
    }

    private final HttpClient cliente = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final String base;

    Api(String base) {
        this.base = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    Resp pedir(String metodo, String ruta, String token, Object json) {
        HttpRequest.Builder b = armar(ruta, token);
        if (json != null) {
            b.header("Content-Type", "application/json");
            return enviar(b, HttpRequest.BodyPublishers.ofString(Json.escribir(json), StandardCharsets.UTF_8), metodo);
        }
        return enviar(b, HttpRequest.BodyPublishers.noBody(), metodo);
    }

    Resp pedir(String metodo, String ruta, String token) {
        return pedir(metodo, ruta, token, null);
    }

    // multipart: campos de texto + un archivo
    Resp subir(String metodo, String ruta, String token, Map<String, ?> campos, String campoArchivo, Path archivo, String tipo) {
        String limite = "----demo" + UUID.randomUUID().toString().replace("-", "");
        ByteArrayOutputStream cuerpo = new ByteArrayOutputStream();
        try {
            for (Map.Entry<String, ?> e : campos.entrySet()) {
                cuerpo.write(("--" + limite + "\r\nContent-Disposition: form-data; name=\"" + e.getKey() + "\"\r\n\r\n"
                        + e.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
            cuerpo.write(("--" + limite + "\r\nContent-Disposition: form-data; name=\"" + campoArchivo + "\"; filename=\""
                    + archivo.getFileName() + "\"\r\nContent-Type: " + tipo + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            cuerpo.write(Files.readAllBytes(archivo));
            cuerpo.write(("\r\n--" + limite + "--\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new Falla("No se pudo leer " + archivo + ": " + e.getMessage());
        }
        HttpRequest.Builder b = armar(ruta, token).header("Content-Type", "multipart/form-data; boundary=" + limite);
        return enviar(b, HttpRequest.BodyPublishers.ofByteArray(cuerpo.toByteArray()), metodo);
    }

    private HttpRequest.Builder armar(String ruta, String token) {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(base + ruta)).timeout(Duration.ofSeconds(90));
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b;
    }

    private Resp enviar(HttpRequest.Builder b, HttpRequest.BodyPublisher cuerpo, String metodo) {
        HttpRequest pedido = b.method(metodo, cuerpo).build();
        for (int intento = 1; ; intento++) {
            try {
                HttpResponse<byte[]> r = cliente.send(pedido, HttpResponse.BodyHandlers.ofByteArray());
                String texto = new String(r.body(), StandardCharsets.UTF_8);
                Object datos;
                if (texto.isEmpty()) {
                    datos = null;
                } else {
                    try {
                        datos = Json.leer(texto);
                    } catch (RuntimeException e) {
                        datos = texto;
                    }
                }
                return new Resp(r.statusCode(), datos);
            } catch (IOException e) {
                if (intento == 2) throw new Falla("No se pudo hablar con el back (" + pedido.uri() + "): " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new Falla("Interrumpido");
            }
        }
    }

    // Devuelve la respuesta si el codigo es uno de los esperados; si no, corta con un mensaje claro.
    static Resp ok(Resp r, String etiqueta, int... esperados) {
        for (int e : esperados) if (r.estado() == e) return r;
        String detalle = String.valueOf(r.cuerpo());
        throw new Falla(etiqueta + ": el back respondio " + r.estado() + " " + (detalle.length() > 200 ? detalle.substring(0, 200) : detalle));
    }
}
