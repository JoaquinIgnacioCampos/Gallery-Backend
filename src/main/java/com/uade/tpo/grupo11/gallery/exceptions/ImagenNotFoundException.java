package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza cuando se pide una imagen que no existe. El handler global la traduce a 404.
public class ImagenNotFoundException extends RuntimeException {
    public ImagenNotFoundException(Long imagenId) {
        super("No existe imagen con id " + imagenId);
    }
}
