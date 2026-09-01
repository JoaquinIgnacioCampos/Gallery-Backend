package com.uade.tpo.grupo11.gallery.controllers.ItemCarrito;

import lombok.Data;

@Data
public class ItemCarritoRequest {

    private Long marcoId;
    private Long varianteId;
    private Long carritoId;
    private Integer cantidad;
}
