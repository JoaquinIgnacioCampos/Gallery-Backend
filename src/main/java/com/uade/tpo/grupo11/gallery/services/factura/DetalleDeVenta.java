package com.uade.tpo.grupo11.gallery.services.factura;

import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;

import java.util.List;

// Lo que se ve al abrir una venta: la factura (con su compra y quien compro) y cada obra vendida
// con su portada, todo armado de una vez para no tener que pedirlo obra por obra.
public record DetalleDeVenta(
        Factura factura,
        List<Linea> lineas
) {
    public record Linea(ItemFactura item, String portadaUrl) {
    }
}
