package com.uade.tpo.grupo11.gallery.exceptions;

public class VarianteDuplicadaException extends RuntimeException {
    public VarianteDuplicadaException(Long obraId, String nombreTamanio) {
        super("La obra con id " + obraId + " ya tiene una variante del tamaño " + nombreTamanio
                + ": cada variante corresponde a un tamaño distinto");
    }
}
