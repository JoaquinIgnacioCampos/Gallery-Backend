package com.uade.tpo.grupo11.gallery.exceptions;

public class EstiloNotFoundException extends RuntimeException {
    public EstiloNotFoundException(Long id) {
        super("No se encontró el estilo con el ID: " + id);
    }
}
