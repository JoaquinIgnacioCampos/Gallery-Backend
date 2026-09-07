package com.uade.tpo.grupo11.gallery.controllers.itemfactura;

import com.uade.tpo.grupo11.gallery.entities.ItemFactura;

import java.math.BigDecimal;

// Lo que la API devuelve de los items de la factura. No exponemos la entidad: evita recursion y datos de mas.
public record ItemFacturaResponse(
        Long id,
        Long factura_id,
        Long marco_id,
        Long variante_id,
        Integer cantidad_items,
        BigDecimal total_item,
        BigDecimal descuento
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static ItemFacturaResponse fromEntity(ItemFactura itemFactura) {
        return new ItemFacturaResponse(
                itemFactura.getId(),
                itemFactura.getFactura().getId(),
                itemFactura.getMarco().getId(),
                itemFactura.getVariante().getId(),
                itemFactura.getCantidad_items(),
                itemFactura.getTotal_item(),
                itemFactura.getDescuento()
        );
    }
}
