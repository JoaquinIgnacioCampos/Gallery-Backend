package com.uade.tpo.grupo11.gallery.exceptions;

public class CompraNotFoundException extends RuntimeException {

    public CompraNotFoundException(Long compraId) {
        super("No existe una compra con id " + compraId);
    }
}
