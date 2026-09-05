package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import lombok.Data;

// DTO de entrada para una actualización parcial.
@Data
public class PerfilArtistaUpdateRequest {
    private Boolean acepta_encargos;
    private String nombre_artistico;
}
