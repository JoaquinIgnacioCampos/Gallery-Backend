package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PerfilArtistaNoAceptaEncargosException extends RuntimeException {

    public PerfilArtistaNoAceptaEncargosException(Long artistaId) {
        super("El artista con id " + artistaId + " no acepta encargos");
    }
}
