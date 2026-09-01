package com.uade.tpo.grupo11.gallery.controllers.Tamanio_Lienzo;

import lombok.Data;

@Data
public class TamanioLienzoRequest {
    private String nombreTamanio;
    private Double anchoLienzo;
    private Double largoLienzo;
}