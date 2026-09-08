package com.uade.tpo.grupo11.gallery.services.carrito;

import java.util.List;

import com.uade.tpo.grupo11.gallery.controllers.carrito.CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;

// Contrato: que sabe hacer el servicio de los carritos. La implementacion es la que lleva la logica.
public interface CarritoService {

    // Devuelve los carritos.
    List<Carrito> getCarritos();

    // Busca el carrito por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Carrito getCarritoById(Long carritoId);

    // Crea el carrito con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Carrito createCarrito(CarritoRequest request);

    // Actualiza el carrito: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    Carrito updateCarrito(Long carritoId, CarritoRequest request);

    // Devuelve las lineas del carrito: que variante, cuantas unidades y con que marco.
    List<ItemCarrito> getItemsByCarrito(Long carritoId);

    // Saca todos los items del carrito sin borrar el carrito.
    void vaciarCarrito(Long carritoId);

    // Devuelve el carrito del usuario y, si todavia no tiene, se lo crea.
    Carrito getOrCreateCarritoByUsuario(Long usuarioId);
}
