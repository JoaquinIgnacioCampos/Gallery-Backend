package com.uade.tpo.grupo11.gallery.controllers.factura;

import com.uade.tpo.grupo11.gallery.entities.Factura;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Lo que la API devuelve de las facturas. No exponemos la entidad: evita recursion y datos de mas.
public record FacturaResponse(
        Long id,
        Long artista_id,
        Long compra_id,
        String detalle_factura,
        BigDecimal precio_total_factura,
        LocalDateTime fecha_creacion_factura
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
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
