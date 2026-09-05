package com.uade.tpo.grupo11.gallery.services.itemfactura;

import com.uade.tpo.grupo11.gallery.controllers.itemfactura.ItemFacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;

import java.util.List;

public interface ItemFacturaService {
    ItemFactura getItemFacturaById(Long id);
    List<ItemFactura> getItemFacturasByFactura(Long facturaId);
    ItemFactura createItemFactura(ItemFacturaRequest request);
}
