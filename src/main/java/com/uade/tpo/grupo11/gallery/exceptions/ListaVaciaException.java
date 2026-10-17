package com.uade.tpo.grupo11.gallery.exceptions;

// Se pidio una lista y no tiene elementos. No es un error: el handler responde 204 No Content,
// sin cuerpo. El 404 queda para lo que no existe (una obra, un artista, un carrito).
public class ListaVaciaException extends RuntimeException {

    public ListaVaciaException(String mensaje) {
        super(mensaje);
    }
}
