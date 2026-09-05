package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MarcoNotFoundException extends RuntimeException {

    public MarcoNotFoundException(Long marcoId) {
        super("No existe un marco con id " + marcoId);
    }
}
