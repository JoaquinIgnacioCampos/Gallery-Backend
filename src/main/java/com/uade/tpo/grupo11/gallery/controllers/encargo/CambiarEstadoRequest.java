package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import lombok.Data;

@Data
public class CambiarEstadoRequest {
    private EstadoEncargo nuevoEstado;
}