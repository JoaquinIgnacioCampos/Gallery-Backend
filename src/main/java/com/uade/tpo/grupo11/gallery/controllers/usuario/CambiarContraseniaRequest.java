package com.uade.tpo.grupo11.gallery.controllers.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CambiarContraseniaRequest {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String contrasenia_actual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String contrasenia_nueva;
}