package com.uade.tpo.grupo11.gallery.exceptions;

import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;

public class TransicionEstadoInvalidaException extends RuntimeException {
    public TransicionEstadoInvalidaException(EstadoEncargo actual, EstadoEncargo nuevo) {
        super("No se puede cambiar el encargo de " + actual + " a " + nuevo);
    }
}