package com.uade.tpo.grupo11.gallery.exceptions;

public class EncargoCerradoPrecioException extends RuntimeException {
    public EncargoCerradoPrecioException(Long encargoId) {
        super("El encargo con id " + encargoId + " ya está cerrado: su precio no se puede modificar");
    }
}
