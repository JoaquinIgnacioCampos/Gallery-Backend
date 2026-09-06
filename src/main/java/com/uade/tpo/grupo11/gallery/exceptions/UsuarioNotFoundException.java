package com.uade.tpo.grupo11.gallery.exceptions;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long usuarioId) {
        super("No existe un usuario con id " + usuarioId);
    }
}
