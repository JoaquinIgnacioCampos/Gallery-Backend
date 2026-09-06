package com.uade.tpo.grupo11.gallery.controllers.factura;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FacturaRequest {

    private Long artista_id;
    private Long compra_id;
    private String detalle_factura;
    private BigDecimal precio_total_factura;
    private LocalDateTime fecha_creacion_factura;
}
