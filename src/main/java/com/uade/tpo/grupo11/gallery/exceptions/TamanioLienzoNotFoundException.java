package com.uade.tpo.grupo11.gallery.exceptions;

public class TamanioLienzoNotFoundException extends RuntimeException {
    public TamanioLienzoNotFoundException(Long id) {
        super("No se encontró el tamaño de lienzo con el ID: " + id);
    }
}
