package com.uade.tpo.grupo11.gallery.services.itemCarrito;

import com.uade.tpo.grupo11.gallery.controllers.ItemCarrito.ItemCarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;

import java.util.List;

public interface ItemCarritoService {

    List<ItemCarrito> getItemsCarrito();

    ItemCarrito getItemCarritoById(Long itemId);

    ItemCarrito createItemCarrito(ItemCarritoRequest request);

    ItemCarrito updateItemCarrito(Long itemId, ItemCarritoRequest request);

    void deleteItemCarrito(Long itemId);
}
