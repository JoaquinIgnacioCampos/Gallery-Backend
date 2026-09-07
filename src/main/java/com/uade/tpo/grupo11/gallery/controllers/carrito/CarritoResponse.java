package com.uade.tpo.grupo11.gallery.controllers.carrito;

import com.uade.tpo.grupo11.gallery.entities.Carrito;

// Lo que la API devuelve de los carritos. No exponemos la entidad: evita recursion y datos de mas.
public record CarritoResponse(
        Long id,
        Long usuario_id,
        String direccion_cliente
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static CarritoResponse fromEntity(Carrito carrito) {
        return new CarritoResponse(
                carrito.getId(),
                carrito.getUsuario().getId(),
                carrito.getDireccion_cliente()
        );
    }
}
