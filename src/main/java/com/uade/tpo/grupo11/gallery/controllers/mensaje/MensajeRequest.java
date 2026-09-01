package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import lombok.Data;

@Data
public class MensajeRequest {
    private Long encargoId;
    private Long usuarioEmisorId;
    private String contenido;
}
