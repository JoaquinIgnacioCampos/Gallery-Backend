package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemCarritoNotFoundException extends RuntimeException {

    public ItemCarritoNotFoundException(Long itemId) {
        super("No existe un item de carrito con id " + itemId);
    }
}
