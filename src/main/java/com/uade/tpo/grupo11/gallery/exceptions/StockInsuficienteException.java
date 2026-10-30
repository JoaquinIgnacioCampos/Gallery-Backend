package com.uade.tpo.grupo11.gallery.exceptions;

import com.uade.tpo.grupo11.gallery.entities.Variante;

//Excepcion para la clase de Item_Factura
public class StockInsuficienteException extends RuntimeException {

    // El mensaje que ve la persona: dice de que obra y tamaño se trata y cuantas quedan.
    public StockInsuficienteException(Variante variante, int solicitado, int disponible) {
        super("No hay stock suficiente de \"" + variante.getObra().getNombre_obra() + "\" ("
                + variante.getTamanio().getNombre_tamanio() + "): pediste " + solicitado + " y "
                + (disponible == 1 ? "queda 1." : "quedan " + disponible + "."));
    }

    public StockInsuficienteException(Long varianteId, int solicitado, int disponible) {
        super("Stock insuficiente para la variante " + varianteId +
                ". Solicitado: " + solicitado + ", disponible: " + disponible);
    }
}