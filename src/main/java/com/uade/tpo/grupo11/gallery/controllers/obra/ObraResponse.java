package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.Set;
import java.util.stream.Collectors;

public record ObraResponse(
        Long id,
        String nombre_obra,
        String descripcion_obra,
        boolean en_venta,
        Long artista_id,
        Set<Long> estilo_ids
) {
    public static ObraResponse fromEntity(Obra obra) {
        return new ObraResponse(
                obra.getId(),
                obra.getNombre_obra(),
                obra.getDescripcion_obra(),
                obra.isEn_venta(),
                obra.getArtista().getId(),
                obra.getEstilos().stream().map(Estilo::getId).collect(Collectors.toSet())
        );
    }
}
