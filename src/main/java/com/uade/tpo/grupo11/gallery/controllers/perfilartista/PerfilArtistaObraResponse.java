package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.entities.Obra;

// Vista resumida de una obra para el listado perteneciente a un perfil de artista.
public record PerfilArtistaObraResponse(
        Long id,
        String nombre_obra,
        String descripcion_obra,
        boolean en_venta
) {
    public static PerfilArtistaObraResponse fromEntity(Obra obra) {
        return new PerfilArtistaObraResponse(
                obra.getId(),
                obra.getNombre_obra(),
                obra.getDescripcion_obra(),
                obra.isEn_venta()
        );
    }
}
