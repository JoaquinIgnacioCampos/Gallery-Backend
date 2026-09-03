package com.uade.tpo.grupo11.gallery.controllers.Marco;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarcoRequest {

    private String nombreMarco;
    private String colorMarco;
    private byte[] imagenMarco;
    private BigDecimal precioMarco;
}
