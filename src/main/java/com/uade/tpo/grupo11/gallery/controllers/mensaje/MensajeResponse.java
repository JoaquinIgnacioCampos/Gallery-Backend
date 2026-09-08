package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;

// Lo que la API devuelve de los mensajes. No exponemos la entidad: evita recursion y datos de mas.
public record MensajeResponse(
        Long id,
        Long encargo_id,
        Long usuario_emisor_id,
        String contenido_mensaje
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static MensajeResponse fromEntity(Mensaje mensaje) {
        return new MensajeResponse(
                mensaje.getId(),
                mensaje.getEncargo().getId(),
                mensaje.getEmisor().getId(),
                mensaje.getContenido_mensaje()
        );
    }
}
