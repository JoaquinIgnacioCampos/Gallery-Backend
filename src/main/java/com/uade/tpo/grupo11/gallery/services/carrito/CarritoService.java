package com.uade.tpo.grupo11.gallery.services.carrito;

import java.util.List;

import com.uade.tpo.grupo11.gallery.controllers.carrito.CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

// Contrato: que sabe hacer el servicio de los carritos. La implementacion es la que lleva la logica.
public interface CarritoService {

    List<Carrito> getCarritos(Usuario usuarioActual);

    Carrito getCarritoById(Long carritoId, Usuario usuarioActual);

    Carrito createCarrito(CarritoRequest request, Usuario usuarioActual);

    Carrito updateCarrito(Long carritoId, CarritoRequest request, Usuario usuarioActual);

    List<ItemCarrito> getItemsByCarrito(Long carritoId, Usuario usuarioActual);

    void vaciarCarrito(Long carritoId, Usuario usuarioActual);

    // Devuelve el carrito del usuario y, si todavia no tiene, se lo crea.
    Carrito getOrCreateCarritoByUsuario(Long usuarioId);
}
