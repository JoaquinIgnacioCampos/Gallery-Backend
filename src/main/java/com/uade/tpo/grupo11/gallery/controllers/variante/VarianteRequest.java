package com.uade.tpo.grupo11.gallery.controllers.variante;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

// Lo que el cliente manda para crear o modificar una variante.
// Las relaciones viajan como ids (obra_id, id_tamanio), no como objetos anidados:
// el Service los busca en la base y arma la entidad.
// Las anotaciones controlan que los datos OBLIGATORIOS esten presentes;
// las reglas de negocio (stock no negativo, descuento de 0 a 100) viven en el Service.
@Data
public class VarianteRequest {

    @NotNull(message = "La obra es obligatoria")
    private Long obra_id;

    @NotNull(message = "El tamanio de lienzo es obligatorio")
    private Long id_tamanio;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio_variante;

    @NotNull(message = "El stock es obligatorio")
    private Integer stock_variante;

    // Opcionales: vacio significa que la variante no tiene descuento vigente.
    private Integer porcentaje_descuento;

    private LocalDate descuento_hasta;
}
