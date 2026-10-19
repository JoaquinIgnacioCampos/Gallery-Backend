package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.services.perfilartista.VentasDelArtista;

import java.math.BigDecimal;
import java.util.List;

// Las ventas del artista. Los datos de la pagina (content, totalPages, totalElements, number y size) se
// llaman igual que en cualquier otra lista paginada, y se suma total_vendido: lo vendido en total,
// de todas las facturas del artista y no solo las de esta pagina.
public record VentasResponse(
        List<VentaResumenResponse> content,
        int totalPages,
        long totalElements,
        int number,
        int size,
        BigDecimal total_vendido
) {
    public static VentasResponse fromVentas(VentasDelArtista ventas) {
        return new VentasResponse(
                ventas.ventas().getContent().stream().map(VentaResumenResponse::fromResumen).toList(),
                ventas.ventas().getTotalPages(),
                ventas.ventas().getTotalElements(),
                ventas.ventas().getNumber(),
                ventas.ventas().getSize(),
                ventas.totalVendido()
        );
    }
}
