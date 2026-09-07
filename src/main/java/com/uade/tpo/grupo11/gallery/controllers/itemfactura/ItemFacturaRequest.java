package com.uade.tpo.grupo11.gallery.controllers.itemfactura;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemFacturaRequest {

    @NotNull(message = "La factura es obligatoria")
    private Long factura_id;

    @NotNull(message = "El marco es obligatorio")
    private Long marco_id;

    @NotNull(message = "La variante es obligatoria")
    private Long variante_id;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad_items;
}
