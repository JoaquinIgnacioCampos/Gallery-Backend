package com.uade.tpo.grupo11.gallery.exceptions;

public class ArtistaNoAceptaEncargosException extends RuntimeException {

    public ArtistaNoAceptaEncargosException(Long artistaId) {
        super("El artista con id " + artistaId + " no acepta encargos");
    }
}
