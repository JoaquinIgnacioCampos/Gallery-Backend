package com.uade.tpo.grupo11.gallery.controllers.artista;

import lombok.Data;

// DTO de entrada para una actualización parcial.
@Data
public class ArtistaUpdateRequest {
    private Boolean aceptaEncargos;
    private String nombreArtistico;
}
