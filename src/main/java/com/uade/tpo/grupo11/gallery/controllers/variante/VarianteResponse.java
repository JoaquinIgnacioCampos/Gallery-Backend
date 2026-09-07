package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.math.BigDecimal;
import java.time.LocalDate;

// Lo que la API DEVUELVE de una variante. Manda el id de la obra en lugar del objeto,
// asi el JSON no vuelve a entrar en la obra y de ahi otra vez en sus variantes.
// Agrega nombre_tamanio, que no esta en la tabla variante: se lo pide al tamanio
// relacionado para que el front no tenga que hacer una segunda consulta.
public record VarianteResponse(
        Long id,
        Long obra_id,
        Long id_tamanio,
        String nombre_tamanio,
        BigDecimal precio_variante,
        int stock_variante,
        Integer porcentaje_descuento,
        LocalDate descuento_hasta
) {
    public static VarianteResponse fromEntity(Variante variante) {
        return new VarianteResponse(
                variante.getId(),
                variante.getObra().getId(),
                variante.getTamanio().getId(),
                variante.getTamanio().getNombre_tamanio(),
                variante.getPrecio_variante(),
                variante.getStock_variante(),
                variante.getPorcentaje_descuento(),
                variante.getDescuento_hasta()
        );
    }
}
