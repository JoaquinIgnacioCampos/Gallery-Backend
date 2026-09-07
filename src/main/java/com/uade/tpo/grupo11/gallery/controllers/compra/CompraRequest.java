package com.uade.tpo.grupo11.gallery.controllers.compra;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompraRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuario_id;

    @NotNull(message = "La fecha de compra es obligatoria")
    private LocalDateTime fecha_compra;
}
