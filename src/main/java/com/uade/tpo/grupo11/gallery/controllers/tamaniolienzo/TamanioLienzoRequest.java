package com.uade.tpo.grupo11.gallery.controllers.tamaniolienzo;

import lombok.Data;

@Data
public class TamanioLienzoRequest {
    private String nombre_tamanio;
    private Double ancho_lienzo;
    private Double largo_lienzo;
}
