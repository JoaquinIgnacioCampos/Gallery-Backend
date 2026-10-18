package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.services.perfilartista.ObraPropiaResumen;

import java.util.List;

// Una obra en la lista "Mis obras" del artista. Manda ids de estilos (los nombres salen de la lista
// de estilos, que se pide una sola vez), la URL de la portada y el resumen de las variantes.
// El detalle de las variantes se pide recien cuando el artista abre la obra.
public record MiObraResponse(
        Long id,
        String nombre_obra,
        String descripcion_obra,
        boolean en_venta,
        List<Long> estilo_ids,
        String portada_url,
        long cantidad_variantes,
        long variantes_sin_stock
) {
    public static MiObraResponse fromResumen(ObraPropiaResumen resumen) {
        return new MiObraResponse(
                resumen.obra().getId(),
                resumen.obra().getNombre_obra(),
                resumen.obra().getDescripcion_obra(),
                resumen.obra().isEn_venta(),
                resumen.estiloIds(),
                resumen.portadaUrl(),
                resumen.cantidadVariantes(),
                resumen.variantesSinStock()
        );
    }
}
