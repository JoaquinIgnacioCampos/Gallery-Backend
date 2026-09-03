package com.uade.tpo.grupo11.gallery.exceptions;

public class ImagenNotFoundException extends RuntimeException {
    public ImagenNotFoundException(Long imagenId) {
        super("No existe imagen con id" + imagenId);
    }
}
