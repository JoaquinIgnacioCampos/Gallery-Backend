package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.entities.Factura;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

// Las ventas de un artista: la pagina de facturas que se pidio y lo vendido en total
// (de todas sus facturas, no solo las de esta pagina).
public record VentasDelArtista(
        Page<Factura> facturas,
        BigDecimal totalVendido
) {
}
