package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No existe un usuario con el id ingresado")
public class UsuarioNotFoundException extends Exception {}
