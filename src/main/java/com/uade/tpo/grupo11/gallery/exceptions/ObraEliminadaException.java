package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza al querer comprar o modificar algo de una obra que el artista dio de baja.
// La obra queda guardada solo como registro: ni ella ni sus variantes o imagenes se tocan mas.
public class ObraEliminadaException extends RuntimeException {

    public ObraEliminadaException(Long obraId) {
        super("La obra con id " + obraId + " fue eliminada: ya no se puede modificar");
    }

    public ObraEliminadaException(Long varianteId, Long obraId) {
        super("No se puede comprar la variante con id " + varianteId
                + ": la obra con id " + obraId + " fue eliminada");
    }
}
