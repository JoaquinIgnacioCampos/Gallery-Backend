package com.uade.tpo.grupo11.gallery.services.itemFactura;

import com.uade.tpo.grupo11.gallery.controllers.ItemFactura.ItemFacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;

import java.util.List;

public interface ItemFacturaService {
    ItemFactura obtenerPorId(Long id);
    List<ItemFactura> obtenerPorFactura(Long facturaId);
    ItemFactura crearItemFactura(ItemFacturaRequest request);
}
