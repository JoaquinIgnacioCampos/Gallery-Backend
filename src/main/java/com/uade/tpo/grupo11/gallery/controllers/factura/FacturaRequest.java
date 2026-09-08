package com.uade.tpo.grupo11.gallery.controllers.factura;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

// Lo que el cliente manda para crear o modificar las facturas. Las relaciones viajan como ids.
@Data
public class FacturaRequest {

    @NotNull(message = "El artista es obligatorio")
    private Long artista_id;

    @NotNull(message = "La compra es obligatoria")
    private Long compra_id;

    private String detalle_factura;

    @NotNull(message = "La fecha de creacion es obligatoria")
    private LocalDateTime fecha_creacion_factura;
}
