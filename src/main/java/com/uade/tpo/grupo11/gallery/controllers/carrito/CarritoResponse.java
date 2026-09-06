package com.uade.tpo.grupo11.gallery.controllers.carrito;

import com.uade.tpo.grupo11.gallery.entities.Carrito;

public record CarritoResponse(
        Long id,
        Long usuario_id,
        String direccion_cliente
) {
    public static CarritoResponse fromEntity(Carrito carrito) {
        return new CarritoResponse(
                carrito.getId(),
                carrito.getUsuario().getId(),
                carrito.getDireccion_cliente()
        );
    }
}
