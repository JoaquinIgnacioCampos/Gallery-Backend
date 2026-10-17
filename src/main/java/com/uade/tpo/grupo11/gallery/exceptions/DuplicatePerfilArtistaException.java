package com.uade.tpo.grupo11.gallery.exceptions;

public class DuplicatePerfilArtistaException extends RuntimeException {

    // Solo se lanza al crear el propio perfil: el mensaje le habla al usuario.
    public DuplicatePerfilArtistaException() {
        super("Ya tenés un perfil de artista creado");
    }
}
