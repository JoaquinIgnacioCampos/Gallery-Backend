package com.uade.tpo.grupo11.gallery.exceptions;

public class VarianteNotFoundException extends RuntimeException {

    public VarianteNotFoundException(Long varianteId) {

        super("No existe variante con id" + varianteId);
    }
}
