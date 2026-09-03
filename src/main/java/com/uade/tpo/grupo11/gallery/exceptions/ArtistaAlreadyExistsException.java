package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ArtistaAlreadyExistsException extends RuntimeException {

    public ArtistaAlreadyExistsException(Long usuarioId) {
        super("El usuario con id " + usuarioId + " ya tiene un perfil de artista");
    }
}
