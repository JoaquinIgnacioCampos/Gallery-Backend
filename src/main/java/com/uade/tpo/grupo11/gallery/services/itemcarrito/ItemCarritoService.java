package com.uade.tpo.grupo11.gallery.services.itemcarrito;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;

import java.util.List;

// Contrato: que sabe hacer el servicio de los items del carrito. La implementacion es la que lleva la logica.
public interface ItemCarritoService {

    // Devuelve los items del carrito.
    List<ItemCarrito> getItemsCarrito();

    // Busca el item del carrito por id. Si no existe, se lanza la excepcion y el handler responde 404.
    ItemCarrito getItemCarritoById(Long itemId);

    // Crea el item del carrito con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    ItemCarrito createItemCarrito(ItemCarritoRequest request);

    // Actualiza el item del carrito: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    ItemCarrito updateItemCarrito(Long itemId, ItemCarritoRequest request);

    // Elimina el item del carrito de la base.
    void deleteItemCarrito(Long itemId);
}
