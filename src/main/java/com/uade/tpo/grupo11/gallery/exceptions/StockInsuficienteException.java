package com.uade.tpo.grupo11.gallery.exceptions;

//Excepcion para la clase de Item_Factura
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(Long varianteId, int solicitado, int disponible) {
        super("Stock insuficiente para la variante " + varianteId +
                ". Solicitado: " + solicitado + ", disponible: " + disponible);
    }
}