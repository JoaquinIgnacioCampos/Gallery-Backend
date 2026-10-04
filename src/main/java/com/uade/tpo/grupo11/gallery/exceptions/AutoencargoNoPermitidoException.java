package com.uade.tpo.grupo11.gallery.exceptions;

public class AutoencargoNoPermitidoException extends RuntimeException {
    public AutoencargoNoPermitidoException(Long usuarioId) {
        super("No podés hacerte un encargo a vos mismo");
    }
}
