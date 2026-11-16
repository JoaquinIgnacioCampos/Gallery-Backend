package com.uade.tpo.grupo11.gallery.controllers.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Las credenciales que manda el cliente para loguearse.
@Data
public class AuthenticationRequest {
    @NotBlank(message = "El email es obligatorio")
    private String email_usuario;
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasenia_usuario;
}