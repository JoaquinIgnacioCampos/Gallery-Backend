package com.uade.tpo.grupo11.gallery.exceptions;

public class PerfilArtistaNotFoundException extends RuntimeException {

    public PerfilArtistaNotFoundException(Long id) {
        super("No existe un perfil de artista con id " + id);
    }
}
