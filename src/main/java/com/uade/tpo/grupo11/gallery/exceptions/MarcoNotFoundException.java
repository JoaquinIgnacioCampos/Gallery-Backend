package com.uade.tpo.grupo11.gallery.exceptions;

public class MarcoNotFoundException extends RuntimeException {

    public MarcoNotFoundException(Long marcoId) {
        super("No existe un marco con id " + marcoId);
    }
}
