package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.List;

// Lo que necesita la lista "Mis obras" de cada obra: la obra y lo que se calcula con sus
// estilos, fotos y variantes, para no tener que pedirlo obra por obra.
public record ObraPropiaResumen(
        Obra obra,
        List<Long> estiloIds,
        String portadaUrl,
        long cantidadVariantes,
        long variantesSinStock
) {
}
