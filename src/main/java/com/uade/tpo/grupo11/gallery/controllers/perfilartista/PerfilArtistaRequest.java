package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilArtistaRequest {

    @NotBlank(message = "El nombre artistico es obligatorio")
    @Size(max = 150, message = "El nombre artistico no puede superar los 150 caracteres")
    private String nombre_artistico;

    private boolean acepta_encargos;
}
