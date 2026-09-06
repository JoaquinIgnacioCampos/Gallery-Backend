package com.uade.tpo.grupo11.gallery.controllers.itemfactura;

import lombok.Data;

@Data
public class ItemFacturaRequest {
    private Long factura_id;
    private Long marco_id;
    private Long variante_id;
    private Integer cantidad_items;
}
