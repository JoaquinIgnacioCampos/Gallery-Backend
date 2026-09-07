package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;

// DTO de salida: evita exponer la entidad Usuario y las relaciones JPA.
public record PerfilArtistaResponse(
        Long id,
        Long usuario_id,
        String nombre_artistico,
        boolean acepta_encargos
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static PerfilArtistaResponse fromEntity(PerfilArtista perfilArtista) {
        return new PerfilArtistaResponse(
                perfilArtista.getId(),
                perfilArtista.getUsuario().getId(),
                perfilArtista.getNombre_artistico(),
                perfilArtista.isAcepta_encargos()
        );
    }
}
