package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza al intentar borrar una obra que todavia tiene variantes o imagenes.
// Sin esto, MySQL rechaza el borrado por la clave foranea y el cliente ve un 500 sin explicacion.
public class ObraEnUsoException extends RuntimeException {

    public ObraEnUsoException(Long obraId) {
        super("No se puede eliminar la obra con id " + obraId
                + ": primero hay que eliminar sus variantes e imagenes");
    }
}
