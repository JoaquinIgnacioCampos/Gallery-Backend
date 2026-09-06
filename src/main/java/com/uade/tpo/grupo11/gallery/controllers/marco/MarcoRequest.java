package com.uade.tpo.grupo11.gallery.controllers.marco;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarcoRequest {

    private String nombre_marco;
    private String color_marco;
    private byte[] imagen_marco;
    private BigDecimal precio_marco;
}
