package com.uade.tpo.grupo11.gallery.controllers.variante;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VarianteRequest {

    private Long obra_id;
    private Long id_tamanio;
    private BigDecimal precio_variante;
    private Integer stock_variante;
    private Integer porcentaje_descuento;
    private LocalDate descuento_hasta;
}
