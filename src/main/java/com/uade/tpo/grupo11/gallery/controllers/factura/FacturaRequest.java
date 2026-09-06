package com.uade.tpo.grupo11.gallery.controllers.factura;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FacturaRequest {

    private Long artista_id;
    private Long compra_id;
    private String detalle_factura;
    private LocalDateTime fecha_creacion_factura;
}
