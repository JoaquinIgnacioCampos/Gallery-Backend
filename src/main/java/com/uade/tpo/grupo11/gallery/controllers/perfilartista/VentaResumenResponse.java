package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.services.perfilartista.VentaResumen;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Una venta en la lista "Mis ventas" del artista: solo lo que hace falta para mostrar la fila.
// El detalle (comprador, entrega y obras vendidas) se pide recien cuando el artista abre la venta,
// en GET /api/facturas/{id}/detalle.
public record VentaResumenResponse(
        Long id,
        Long compra_id,
        LocalDateTime fecha_creacion_factura,
        BigDecimal precio_total_factura,
        long cantidad_obras
) {
    public static VentaResumenResponse fromResumen(VentaResumen resumen) {
        return new VentaResumenResponse(
                resumen.venta().getId(),
                resumen.venta().getCompraId(),
                resumen.venta().getFecha(),
                resumen.venta().getTotal(),
                resumen.cantidadObras()
        );
    }
}
