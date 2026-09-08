package com.uade.tpo.grupo11.gallery.services.itemcarrito;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;

// Contrato: que sabe hacer el servicio de los items del carrito. La implementacion es la que lleva la logica.
public interface ItemCarritoService {

    List<ItemCarrito> getItemsCarrito(Usuario usuarioActual);

    ItemCarrito getItemCarritoById(Long itemId, Usuario usuarioActual);

    ItemCarrito createItemCarrito(ItemCarritoRequest request, Usuario usuarioActual);

    ItemCarrito updateItemCarrito(Long itemId, ItemCarritoRequest request, Usuario usuarioActual);

    void deleteItemCarrito(Long itemId, Usuario usuarioActual);
}
