package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Lo que el cliente manda para crear o modificar los items del carrito. Las relaciones viajan como ids.
@Data
public class ItemCarritoRequest {

    @NotNull(message = "El marco es obligatorio")
    private Long marco_id;

    @NotNull(message = "La variante es obligatoria")
    private Long variante_id;

    @NotNull(message = "El carrito es obligatorio")
    private Long carrito_id;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
}
