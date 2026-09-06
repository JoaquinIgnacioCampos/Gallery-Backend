package com.uade.tpo.grupo11.gallery.exceptions;

public class FacturaNotFoundException extends RuntimeException {

    public FacturaNotFoundException(Long facturaId) {
        super("No existe una factura con id " + facturaId);
    }
}
