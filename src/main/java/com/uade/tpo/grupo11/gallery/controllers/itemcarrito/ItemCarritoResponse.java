package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;

// Lo que la API devuelve de los items del carrito. No exponemos la entidad: evita recursion y datos de mas.
public record ItemCarritoResponse(
        Long id,
        Long marco_id,
        Long variante_id,
        Long carrito_id,
        Integer cantidad
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
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
