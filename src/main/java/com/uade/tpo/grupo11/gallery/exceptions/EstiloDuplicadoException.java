package com.uade.tpo.grupo11.gallery.exceptions;

public class EstiloDuplicadoException extends RuntimeException {
    public EstiloDuplicadoException(String nombreEstilo) {
        super("Ya existe un estilo con el nombre: " + nombreEstilo);
    }
}