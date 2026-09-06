package com.uade.tpo.grupo11.gallery.exceptions;

public class ItemCarritoNotFoundException extends RuntimeException {

    public ItemCarritoNotFoundException(Long itemId) {
        super("No existe un item de carrito con id " + itemId);
    }
}
