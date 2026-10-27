package com.uade.tpo.grupo11.gallery.entities.enums;

public enum TipoPintura {
    OLEO("Óleo"),
    ACRILICO("Acrílico"),
    ACUARELA("Acuarela"),
    GRAFITI("Grafiti");

    // El texto que ve la persona. Vive acá para que el front no repita la lista.
    private final String nombre;

    TipoPintura(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
