package com.uade.tpo.grupo11.gallery.exceptions;

public class EncargoCanceladoException extends RuntimeException {
    public EncargoCanceladoException(Long encargoId) {
        super("El encargo con id " + encargoId + " está cancelado, no se pueden enviar más mensajes");
    }
}