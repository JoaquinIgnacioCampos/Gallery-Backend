package com.uade.tpo.grupo11.gallery.controllers.auth;

import lombok.Data;

// Las credenciales que manda el cliente para loguearse.
@Data
public class AuthenticationRequest {
    private String email_usuario;
    private String contrasenia_usuario;
}