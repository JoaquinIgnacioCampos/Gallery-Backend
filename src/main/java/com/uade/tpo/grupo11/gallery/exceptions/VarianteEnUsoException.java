package com.uade.tpo.grupo11.gallery.exceptions;

public class VarianteEnUsoException extends RuntimeException {
    public VarianteEnUsoException(Long varianteId) {
        super("El tamaño con id " + varianteId + " ya se vendió o está en un carrito: no se puede quitar. "
                + "Para dejar de venderlo, poné su stock en 0.");
    }
}
