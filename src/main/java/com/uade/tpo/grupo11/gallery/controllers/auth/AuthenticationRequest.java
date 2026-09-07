package com.uade.tpo.grupo11.gallery.controllers.auth;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email_usuario;
    private String contrasenia_usuario;
}