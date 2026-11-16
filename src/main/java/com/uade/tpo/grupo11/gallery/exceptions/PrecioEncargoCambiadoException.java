package com.uade.tpo.grupo11.gallery.exceptions;

import java.math.BigDecimal;

public class PrecioEncargoCambiadoException extends RuntimeException {
    public PrecioEncargoCambiadoException(Long encargoId, BigDecimal precioActual) {
        super("El artista cambio el precio del encargo con id " + encargoId + ": ahora es " + precioActual
                + ". Revisalo antes de aceptar.");
    }
}
