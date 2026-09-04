package com.uade.tpo.grupo11.gallery.exceptions;

public class CompraNotFoundException extends RuntimeException {
    public CompraNotFoundException(Long CompraId) {

        super("No existe una compra con id " + compraId);
    }
}

