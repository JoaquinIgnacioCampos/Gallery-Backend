package com.uade.tpo.grupo11.gallery.config;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Escribe las respuestas de error de seguridad con el mismo formato que el resto de la API:
// {"mensaje": "..."}. Hace falta porque estos errores ocurren en los filtros, antes de
// llegar a los controllers, y ahi el GlobalExceptionHandler no interviene.
final class RespuestaSeguridad {

    private RespuestaSeguridad() {}

    static void escribir(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // Los mensajes son textos fijos nuestros, sin comillas: no hace falta un serializador.
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }
}
