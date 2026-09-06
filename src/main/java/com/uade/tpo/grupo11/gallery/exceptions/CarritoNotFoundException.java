package com.uade.tpo.grupo11.gallery.exceptions;

public class CarritoNotFoundException extends RuntimeException {

    public CarritoNotFoundException(Long carritoId) {
        super("No existe un carrito con id " + carritoId);
    }
}
