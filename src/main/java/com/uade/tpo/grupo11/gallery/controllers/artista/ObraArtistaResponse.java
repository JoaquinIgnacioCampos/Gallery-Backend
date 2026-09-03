package com.uade.tpo.grupo11.gallery.controllers.artista;

import com.uade.tpo.grupo11.gallery.entities.Obra;

// Vista resumida de una obra para el listado perteneciente a un artista.
public record ObraArtistaResponse(
        Long id,
        String nombreObra,
        String descripcionObra,
        boolean enVenta
) {
    public static ObraArtistaResponse fromEntity(Obra obra) {
        return new ObraArtistaResponse(
                obra.getId(),
                obra.getNombreObra(),
                obra.getDescripcionObra(),
                obra.isEnVenta()
        );
    }
}
