package com.uade.tpo.grupo11.gallery.services.itemCarrito;

import com.uade.tpo.grupo11.gallery.controllers.Item_Carrito.Item_CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Item_Carrito;

import java.util.List;

public interface Item_CarritoService {

    List<Item_Carrito> getItemsCarrito();

    Item_Carrito getItemCarritoById(Long itemId);

    Item_Carrito createItemCarrito(Item_CarritoRequest request);

    Item_Carrito updateItemCarrito(Long itemId, Item_CarritoRequest request);

    void deleteItemCarrito(Long itemId);
}
