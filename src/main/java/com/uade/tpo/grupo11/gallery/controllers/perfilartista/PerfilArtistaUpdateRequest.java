package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO de entrada para una actualización parcial.
@Data
public class PerfilArtistaUpdateRequest {
    private Boolean acepta_encargos;

    @Size(max = 150, message = "El nombre artistico no puede superar los 150 caracteres")
    private String nombre_artistico;
}
