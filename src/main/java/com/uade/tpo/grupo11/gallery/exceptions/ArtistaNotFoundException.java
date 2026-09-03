package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArtistaNotFoundException extends RuntimeException {

    public ArtistaNotFoundException(Long artistaId) {
        super("No existe un artista con id " + artistaId);
    }
}
