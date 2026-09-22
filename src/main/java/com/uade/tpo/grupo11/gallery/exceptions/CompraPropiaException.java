package com.uade.tpo.grupo11.gallery.exceptions;

public class CompraPropiaException extends RuntimeException {

    public CompraPropiaException(Long varianteId) {
        super("No podes agregar al carrito la variante con id " + varianteId + ": es de tu propia obra");
    }
}
