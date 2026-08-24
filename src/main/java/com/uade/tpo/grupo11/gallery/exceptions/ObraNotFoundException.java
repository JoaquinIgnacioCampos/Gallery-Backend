package com.uade.tpo.grupo11.gallery.exceptions;

// RuntimeException: no obliga a poner try/catch en cada llamada.
public class ObraNotFoundException extends RuntimeException {

    public ObraNotFoundException(Long obraId) {
        super("No existe una obra con id " + obraId);
    }
}