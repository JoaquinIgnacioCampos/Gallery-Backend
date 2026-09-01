package com.uade.tpo.grupo11.gallery.exceptions;

public class EncargoNotFoundException extends RuntimeException {
    public EncargoNotFoundException(Long id) {
        super("No se encontró el encargo con el ID: " + id);
    }
}
