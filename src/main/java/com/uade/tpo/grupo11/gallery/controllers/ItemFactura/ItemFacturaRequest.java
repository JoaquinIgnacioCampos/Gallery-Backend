package com.uade.tpo.grupo11.gallery.controllers.ItemFactura;

import lombok.Data;

@Data
public class ItemFacturaRequest {
    private Long facturaId;
    private Long marcoId;
    private Long varianteId;
    private Integer cantidadItems;
}
