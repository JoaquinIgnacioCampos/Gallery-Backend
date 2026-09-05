package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import lombok.Data;

@Data
public class MensajeRequest {
    private Long encargo_id;
    private Long usuario_emisor_id;
    private String contenido;
}
