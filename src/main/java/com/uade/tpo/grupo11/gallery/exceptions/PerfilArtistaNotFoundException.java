package com.uade.tpo.grupo11.gallery.exceptions;

public class PerfilArtistaNotFoundException extends RuntimeException {

    public PerfilArtistaNotFoundException(Long id) {
        super("No existe un perfil de artista con id " + id);
    }

    // Cuando el id del perfil no aplica: el perfil se busca por usuario o es el propio.
    public PerfilArtistaNotFoundException(String mensaje) {
        super(mensaje);
    }
}
