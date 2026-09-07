package com.uade.tpo.grupo11.gallery.controllers.estilo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EstiloRequest {

    @NotBlank(message = "El nombre del estilo es obligatorio")
    private String nombreEstilo;
}
