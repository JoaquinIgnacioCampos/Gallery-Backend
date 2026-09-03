package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El usuario ingresado ya tiene un perfil de artista creado")
public class DuplicatePerfilArtistaException extends Exception {}
