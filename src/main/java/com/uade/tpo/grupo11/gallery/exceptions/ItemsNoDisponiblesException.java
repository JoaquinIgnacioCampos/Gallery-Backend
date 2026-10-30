package com.uade.tpo.grupo11.gallery.exceptions;

import java.util.List;

// El checkout se rechaza porque hay items del carrito que ya no estan disponibles (obra sacada de la venta o sin
// stock suficiente). Lleva TODOS los items con problema, no solo el primero, para que el front los marque.
public class ItemsNoDisponiblesException extends RuntimeException {

    private final List<ProblemaDeItem> items;

    public ItemsNoDisponiblesException(List<ProblemaDeItem> items) {
        super("Hay obras de tu carrito que ya no están disponibles. Revisalas antes de pagar.");
        this.items = items;
    }

    public List<ProblemaDeItem> getItems() {
        return items;
    }
}
