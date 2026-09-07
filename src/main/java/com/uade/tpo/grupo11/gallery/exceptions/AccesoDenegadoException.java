package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza cuando el usuario esta autenticado y tiene el rol correcto, pero el recurso
// que quiere tocar no es suyo. Es el chequeo de PERTENENCIA, que el rol solo no cubre:
// ser artista no te habilita a manejar los encargos de otro artista.
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
