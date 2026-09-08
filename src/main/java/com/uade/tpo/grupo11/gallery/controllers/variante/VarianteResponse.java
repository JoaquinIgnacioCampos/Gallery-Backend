package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.math.BigDecimal;
import java.time.LocalDate;

// Lo que la API devuelve de las variantes. No exponemos la entidad: evita recursion y datos de mas.
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
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
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
