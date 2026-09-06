package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import lombok.Data;

@Data
public class ItemCarritoRequest {

    private Long marco_id;
    private Long variante_id;
    private Long carrito_id;
    private Integer cantidad;
}
