package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarritoNotFoundException extends RuntimeException {

    public CarritoNotFoundException(Long carritoId) {
        super("No existe un carrito con id " + carritoId);
    }
}
