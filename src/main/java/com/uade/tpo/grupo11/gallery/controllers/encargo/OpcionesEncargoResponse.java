package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;

import java.util.Arrays;
import java.util.List;

// Lo que se puede elegir al pedir un encargo: los valores de los enums, con su texto.
public record OpcionesEncargoResponse(
        List<OpcionResponse> tipos_pintura,
        List<OpcionResponse> tipos_lienzo
) {
    public static OpcionesEncargoResponse conTodasLasOpciones() {
        return new OpcionesEncargoResponse(
                Arrays.stream(TipoPintura.values()).map(tipo -> new OpcionResponse(tipo.name(), tipo.getNombre())).toList(),
                Arrays.stream(TipoLienzo.values()).map(tipo -> new OpcionResponse(tipo.name(), tipo.getNombre())).toList()
        );
    }
}
