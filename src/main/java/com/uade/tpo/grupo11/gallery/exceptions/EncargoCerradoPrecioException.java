package com.uade.tpo.grupo11.gallery.exceptions;

public class EncargoCerradoPrecioException extends RuntimeException {
    public EncargoCerradoPrecioException(Long encargoId) {
        super("El encargo con id " + encargoId + " ya tiene el precio congelado: no se puede modificar");
    }
}
