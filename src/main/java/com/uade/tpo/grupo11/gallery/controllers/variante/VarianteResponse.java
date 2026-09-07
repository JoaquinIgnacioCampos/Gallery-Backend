package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.math.BigDecimal;
import java.time.LocalDate;

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
