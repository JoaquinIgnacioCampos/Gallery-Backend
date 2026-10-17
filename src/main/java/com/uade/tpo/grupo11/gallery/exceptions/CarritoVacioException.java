package com.uade.tpo.grupo11.gallery.exceptions;

// No se puede hacer el checkout de un carrito sin items.
public class CarritoVacioException extends RuntimeException {

    public CarritoVacioException(Long carritoId) {
        super("El carrito con id " + carritoId + " esta vacio: no hay nada para comprar");
    }

    // Cuando es el carrito del propio usuario.
    public CarritoVacioException() {
        super("Tu carrito está vacío: no hay nada para comprar");
    }
}
