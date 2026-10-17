package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza al querer comprar una variante de una obra que el artista saco de la venta.
public class ObraNoEnVentaException extends RuntimeException {

    public ObraNoEnVentaException(Long varianteId, Long obraId) {
        super("No se puede comprar la variante con id " + varianteId
                + ": la obra con id " + obraId + " no esta en venta");
    }
}
