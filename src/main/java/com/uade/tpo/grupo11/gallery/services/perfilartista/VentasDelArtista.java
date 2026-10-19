package com.uade.tpo.grupo11.gallery.services.perfilartista;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;

// Las ventas de un artista: la pagina de ventas que se pidio y lo vendido en total
// (de todas sus facturas, no solo las de esta pagina).
public record VentasDelArtista(
        Page<VentaResumen> ventas,
        BigDecimal totalVendido
) {
}
