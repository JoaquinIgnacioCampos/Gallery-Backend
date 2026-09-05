package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CompraNotFoundException extends RuntimeException {

    public CompraNotFoundException(Long compraId) {
        super("No existe una compra con id " + compraId);
    }
}
