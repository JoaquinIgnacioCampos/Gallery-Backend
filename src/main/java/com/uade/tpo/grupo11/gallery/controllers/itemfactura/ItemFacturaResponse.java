package com.uade.tpo.grupo11.gallery.controllers.itemfactura;

import com.uade.tpo.grupo11.gallery.entities.ItemFactura;

import java.math.BigDecimal;

public record ItemFacturaResponse(
        Long id,
        Long factura_id,
        Long marco_id,
        Long variante_id,
        Integer cantidad_items,
        BigDecimal total_item,
        BigDecimal descuento
) {
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
