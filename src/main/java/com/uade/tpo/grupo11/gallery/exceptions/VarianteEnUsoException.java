package com.uade.tpo.grupo11.gallery.exceptions;

public class VarianteEnUsoException extends RuntimeException {
    public VarianteEnUsoException(String nombreTamanio) {
        super("El tamaño " + nombreTamanio + " ya se vendió o está en un carrito: no se puede quitar. "
                + "Para dejar de venderlo, poné su stock en 0.");
    }
}
