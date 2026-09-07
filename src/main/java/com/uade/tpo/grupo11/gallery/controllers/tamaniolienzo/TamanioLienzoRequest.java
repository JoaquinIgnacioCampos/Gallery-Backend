package com.uade.tpo.grupo11.gallery.controllers.tamaniolienzo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TamanioLienzoRequest {

    @NotBlank(message = "El nombre del tamanio es obligatorio")
    private String nombre_tamanio;

    @NotNull(message = "El ancho del lienzo es obligatorio")
    private Double ancho_lienzo;

    @NotNull(message = "El largo del lienzo es obligatorio")
    private Double largo_lienzo;
}
