package com.uade.tpo.grupo11.gallery.exceptions;

public class PrecioEncargoNoDefinidoException extends RuntimeException {
    public PrecioEncargoNoDefinidoException(Long encargoId) {
        super("El encargo con id " + encargoId + " todavía no tiene un precio cargado por el artista");
    }
}
