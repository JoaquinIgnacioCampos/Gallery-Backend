package com.uade.tpo.grupo11.gallery.entities.enums;

public enum TipoLienzo {
    LIENZO("Lienzo"),
    PAPEL("Papel"),
    MADERA("Madera"),
    TELA("Tela");

    // El texto que ve la persona. Vive acá para que el front no repita la lista.
    private final String nombre;

    TipoLienzo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
