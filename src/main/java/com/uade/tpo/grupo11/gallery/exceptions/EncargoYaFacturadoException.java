package com.uade.tpo.grupo11.gallery.exceptions;

public class EncargoYaFacturadoException extends RuntimeException {
    public EncargoYaFacturadoException(Long encargoId) {
        super("El encargo con id " + encargoId + " ya fue facturado");
    }
}
