package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Lo que el cliente manda para crear o modificar los perfiles de artista. Las relaciones viajan como ids.
@Data
public class PerfilArtistaRequest {

    @NotBlank(message = "El nombre artistico es obligatorio")
    @Size(max = 150, message = "El nombre artistico no puede superar los 150 caracteres")
    private String nombre_artistico;

    private boolean acepta_encargos;
}
