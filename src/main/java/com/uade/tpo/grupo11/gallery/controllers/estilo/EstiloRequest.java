package com.uade.tpo.grupo11.gallery.controllers.estilo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Lo que el cliente manda para crear o modificar los estilos. Las relaciones viajan como ids.
@Data
public class EstiloRequest {

    @NotBlank(message = "El nombre del estilo es obligatorio")
    private String nombreEstilo;
}
