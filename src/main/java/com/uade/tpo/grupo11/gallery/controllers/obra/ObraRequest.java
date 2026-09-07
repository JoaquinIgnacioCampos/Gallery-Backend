package com.uade.tpo.grupo11.gallery.controllers.obra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

// Lo que el cliente manda para crear o modificar una obra.
// Las relaciones viajan como ids, no como objetos anidados.
// Las anotaciones controlan que los datos OBLIGATORIOS esten presentes;
// las reglas de negocio (rangos, permisos) siguen viviendo en el Service.
@Data
public class ObraRequest {

    @NotBlank(message = "El nombre de la obra es obligatorio")
    private String nombre_obra;

    private String descripcion_obra;

    @NotNull(message = "Hay que indicar si la obra esta en venta")
    private Boolean en_venta;

    // El artista NO se pide: se toma del usuario logueado. Si viniera en el body,
    // cualquier artista podria publicar una obra a nombre de otro cambiando el numero.

    private Set<Long> estilo_ids;
}
