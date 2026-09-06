package com.uade.tpo.grupo11.gallery.controllers.obra;

import lombok.Data;

import java.util.Set;

// Lo que el cliente manda para crear o modificar una obra.
// Las relaciones viajan como ids, no como objetos anidados.
@Data
public class ObraRequest {

    private String nombre_obra;
    private String descripcion_obra;
    private Boolean en_venta;
    private Long artista_id;
    private Set<Long> estilo_ids;
}
