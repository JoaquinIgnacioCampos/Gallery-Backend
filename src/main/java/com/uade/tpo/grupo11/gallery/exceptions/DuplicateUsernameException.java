package com.uade.tpo.grupo11.gallery.exceptions;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException() {
        super("Existe un usuario con el nombre ingresado");
    }
}
