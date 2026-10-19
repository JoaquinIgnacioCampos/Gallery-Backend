package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaResponse;
import com.uade.tpo.grupo11.gallery.services.perfilartista.VentasDelArtista;

import java.math.BigDecimal;
import java.util.List;

// Las ventas del artista. Los datos de la pagina (content, totalPages, totalElements, number y size) se
// llaman igual que en cualquier otra lista paginada, y se suma total_vendido: lo vendido en total,
// de todas las facturas del artista y no solo las de esta pagina.
public record VentasResponse(
        List<FacturaResponse> content,
        int totalPages,
        long totalElements,
        int number,
        int size,
        BigDecimal total_vendido
) {
    public static VentasResponse fromVentas(VentasDelArtista ventas) {
        return new VentasResponse(
                ventas.facturas().getContent().stream().map(FacturaResponse::fromEntity).toList(),
                ventas.facturas().getTotalPages(),
                ventas.facturas().getTotalElements(),
                ventas.facturas().getNumber(),
                ventas.facturas().getSize(),
                ventas.totalVendido()
        );
    }
}
