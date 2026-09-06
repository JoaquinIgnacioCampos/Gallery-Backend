package com.uade.tpo.grupo11.gallery.exceptions;

public class DuplicatePerfilArtistaException extends RuntimeException {

    public DuplicatePerfilArtistaException(Long usuarioId) {
        super("El usuario con id " + usuarioId + " ya tiene un perfil de artista creado");
    }
}
