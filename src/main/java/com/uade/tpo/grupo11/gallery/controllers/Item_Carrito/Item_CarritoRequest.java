package com.uade.tpo.grupo11.gallery.controllers.Item_Carrito;

import lombok.Data;

@Data
public class Item_CarritoRequest {

    private Long marcoId;
    private Long varianteId;
    private Long carritoId;
    private Integer cantidad;
}
