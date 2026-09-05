package com.uade.tpo.grupo11.gallery.services.itemcarrito;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;

import java.util.List;

public interface ItemCarritoService {

    List<ItemCarrito> getItemsCarrito();

    ItemCarrito getItemCarritoById(Long itemId);

    ItemCarrito createItemCarrito(ItemCarritoRequest request);

    ItemCarrito updateItemCarrito(Long itemId, ItemCarritoRequest request);

    void deleteItemCarrito(Long itemId);
}
