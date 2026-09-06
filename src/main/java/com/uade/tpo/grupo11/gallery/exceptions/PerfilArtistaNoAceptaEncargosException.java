package com.uade.tpo.grupo11.gallery.exceptions;

public class PerfilArtistaNoAceptaEncargosException extends RuntimeException {

    public PerfilArtistaNoAceptaEncargosException(Long artistaId) {
        super("El artista con id " + artistaId + " no acepta encargos");
    }
}
