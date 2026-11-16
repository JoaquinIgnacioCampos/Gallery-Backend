package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Lo que el cliente manda para crear o modificar los items del carrito. Las relaciones viajan como ids.
@Data
public class ItemCarritoRequest {

    // Tope de unidades por item. Lo usan tambien el service del carrito y el checkout.
    public static final int CANTIDAD_MAXIMA = 999;

    // Sin marco: null.
    private Long marco_id;

    @NotNull(message = "La variante es obligatoria")
    private Long variante_id;

    @NotNull(message = "El carrito es obligatorio")
    private Long carrito_id;

    // Sin @Min, una cantidad negativa pasaba el chequeo de stock, sumaba stock en el checkout y bajaba el total.
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad tiene que ser al menos 1")
    @Max(value = CANTIDAD_MAXIMA, message = "La cantidad no puede superar 999")
    private Integer cantidad;
}
