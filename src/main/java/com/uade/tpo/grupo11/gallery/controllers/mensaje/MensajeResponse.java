package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;

public record MensajeResponse(
        Long id,
        Long encargo_id,
        Long usuario_emisor_id,
        String contenido_mensaje
) {
    public static MensajeResponse fromEntity(Mensaje mensaje) {
        return new MensajeResponse(
                mensaje.getId(),
                mensaje.getEncargo().getId(),
                mensaje.getEmisor().getId(),
                mensaje.getContenido_mensaje()
        );
    }
}
