package com.uade.tpo.grupo11.gallery.exceptions;

import java.util.UUID;

public class MensajeNotFoundException extends RuntimeException {
    public MensajeNotFoundException(Long id) {
        super("No se encontró el mensaje con el ID: " + id);
    }
}
