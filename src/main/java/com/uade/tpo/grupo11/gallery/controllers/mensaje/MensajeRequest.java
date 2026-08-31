package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import lombok.Data;
import java.util.UUID;

@Data
public class MensajeRequest {
    private UUID encargoId;
    private UUID usuarioEmisorId;
    private String contenido;
}