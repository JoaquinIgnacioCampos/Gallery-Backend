package com.uade.tpo.grupo11.gallery.exceptions;

public class EncargoNoTerminadoException extends RuntimeException {
    public EncargoNoTerminadoException(Long encargoId) {
        super("El encargo con id " + encargoId + " todavía no está terminado, no se puede facturar");
    }
}
