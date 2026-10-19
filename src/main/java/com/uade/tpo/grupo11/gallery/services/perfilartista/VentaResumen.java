package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.repositories.VentaDeArtista;

// Lo que necesita la lista "Mis ventas" de cada factura: lo que muestra la fila y cuantas obras lleva,
// para no tener que pedir los items factura por factura.
public record VentaResumen(
        VentaDeArtista venta,
        long cantidadObras
) {
}
