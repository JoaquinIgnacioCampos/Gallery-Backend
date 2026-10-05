package com.uade.tpo.grupo11.gallery.exceptions;

public class EncargoNoTerminadoException extends RuntimeException {
    public EncargoNoTerminadoException(Long encargoId) {
        super("El encargo con id " + encargoId + " todavía no está en proceso ni terminado, no se puede facturar");
    }
}
