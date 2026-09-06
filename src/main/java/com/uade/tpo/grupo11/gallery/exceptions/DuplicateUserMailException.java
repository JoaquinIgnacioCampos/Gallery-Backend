package com.uade.tpo.grupo11.gallery.exceptions;

public class DuplicateUserMailException extends RuntimeException {

    public DuplicateUserMailException() {
        super("Existe un usuario con el mail ingresado");
    }
}
