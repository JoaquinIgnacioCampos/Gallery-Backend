package com.uade.tpo.grupo11.gallery.exceptions;

public class PrecioEncargoNoAceptadoException extends RuntimeException {
    public PrecioEncargoNoAceptadoException(Long encargoId) {
        super("El cliente todavía no aceptó el precio del encargo con id " + encargoId);
    }
}
