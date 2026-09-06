package com.uade.tpo.grupo11.gallery.controllers.factura;

import com.uade.tpo.grupo11.gallery.entities.Factura;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FacturaResponse(
        Long id,
        Long artista_id,
        Long compra_id,
        String detalle_factura,
        BigDecimal precio_total_factura,
        LocalDateTime fecha_creacion_factura
) {
    public static FacturaResponse fromEntity(Factura factura) {
        return new FacturaResponse(
                factura.getId(),
                factura.getArtista().getId(),
                factura.getCompra().getId(),
                factura.getDetalle_factura(),
                factura.getPrecio_total_factura(),
                factura.getFecha_creacion_factura()
        );
    }
}
