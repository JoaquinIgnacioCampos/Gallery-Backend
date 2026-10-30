package com.uade.tpo.grupo11.gallery.exceptions;

// Un item del carrito que no se puede comprar, tal como lo recibe el front en el 409 del checkout.
//   item_id          : el id del item en el carrito, para marcarlo en la pantalla.
//   cantidad_pedida  : lo que hay en el carrito.
//   stock_disponible : lo que queda (0 si ya no esta a la venta).
public record ProblemaDeItem(
        Long item_id,
        String nombre_obra,
        String nombre_tamanio,
        int cantidad_pedida,
        int stock_disponible,
        String motivo
) {
}
