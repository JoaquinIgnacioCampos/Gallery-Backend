package com.uade.tpo.grupo11.gallery.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Una fila de FacturaRepository.findVentasDelArtista: solo lo que la lista "Mis ventas" muestra de cada factura.
// Spring arma estos objetos solo, a partir de los alias (id, compraId, fecha, total) de la consulta, sin cargar
// la compra ni quien compro.
public interface VentaDeArtista {
    Long getId();
    Long getCompraId();
    LocalDateTime getFecha();
    BigDecimal getTotal();
}
