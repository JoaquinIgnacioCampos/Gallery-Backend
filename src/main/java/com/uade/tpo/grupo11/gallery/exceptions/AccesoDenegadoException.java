package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza cuando el usuario tiene el rol correcto pero el recurso no es suyo.
// Ser artista no te habilita a manejar los encargos de otro artista.
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
