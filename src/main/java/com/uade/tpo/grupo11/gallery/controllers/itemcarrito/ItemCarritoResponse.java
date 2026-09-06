package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;

public record ItemCarritoResponse(
        Long id,
        Long marco_id,
        Long variante_id,
        Long carrito_id,
        Integer cantidad
) {
    public static ItemCarritoResponse fromEntity(ItemCarrito itemCarrito) {
        return new ItemCarritoResponse(
                itemCarrito.getId(),
                itemCarrito.getMarco().getId(),
                itemCarrito.getVariante().getId(),
                itemCarrito.getCarrito().getId(),
                itemCarrito.getCantidad()
        );
    }
}
