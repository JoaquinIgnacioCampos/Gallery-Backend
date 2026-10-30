package com.uade.tpo.grupo11.gallery.exceptions;

import com.uade.tpo.grupo11.gallery.entities.Variante;

public class CompraPropiaException extends RuntimeException {

    public CompraPropiaException(Variante variante) {
        super("No podés comprar tus propias obras: \"" + variante.getObra().getNombre_obra() + "\" es tuya.");
    }
}
