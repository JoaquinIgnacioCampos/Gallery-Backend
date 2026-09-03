package com.uade.tpo.grupo11.gallery.controllers.artista;

import lombok.Data;

// DTO de entrada para crear un perfil de artista.
@Data
public class ArtistaRequest {
    private Long usuarioId;
    private Boolean aceptaEncargos;
    private String nombreArtistico;
}
