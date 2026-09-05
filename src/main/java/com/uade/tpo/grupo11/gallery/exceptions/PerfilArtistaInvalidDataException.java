package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PerfilArtistaInvalidDataException extends RuntimeException {

    public PerfilArtistaInvalidDataException(String message) {
        super(message);
    }
}
