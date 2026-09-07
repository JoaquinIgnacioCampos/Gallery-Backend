package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.Set;
import java.util.stream.Collectors;

// Lo que la API DEVUELVE de una obra. Es un record (clase inmutable, solo lectura).
// Por que no devolvemos la entity directamente:
//  1) corta la recursion del JSON (Obra tiene variantes y cada variante tiene su obra),
//  2) no expone campos que el cliente no tiene por que ver,
//  3) devuelve las relaciones como ids, mas livianas que el objeto entero.
// fromEntity es la traduccion de entidad a respuesta, en un solo lugar.
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
