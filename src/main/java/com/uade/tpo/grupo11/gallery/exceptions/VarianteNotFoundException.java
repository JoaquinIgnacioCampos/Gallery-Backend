package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza cuando se pide una variante que no existe. El handler global la traduce a 404.
public class VarianteNotFoundException extends RuntimeException {

    public VarianteNotFoundException(Long varianteId) {

        super("No existe variante con id " + varianteId);
    }
}
