package com.uade.tpo.grupo11.gallery.controllers.artista;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;

// DTO de salida: evita exponer la entidad Usuario y las relaciones JPA.
public record ArtistaResponse(
        Long id,
        Long usuarioId,
        String nombreArtistico,
        boolean aceptaEncargos
) {
    public static ArtistaResponse fromEntity(PerfilArtista artista) {
        return new ArtistaResponse(
                artista.getId(),
                artista.getUsuario().getUsuario_id(),
                artista.getNombreArtistico(),
                artista.isAceptaEncargos()
        );
    }
}
