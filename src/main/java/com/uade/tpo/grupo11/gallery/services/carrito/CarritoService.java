package com.uade.tpo.grupo11.gallery.services.carrito;

import java.util.List;

import com.uade.tpo.grupo11.gallery.controllers.carrito.CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;

public interface CarritoService {

    List<Carrito> getCarritos();

    Carrito getCarritoById(Long carritoId);

    Carrito createCarrito(CarritoRequest request);

    Carrito updateCarrito(Long carritoId, CarritoRequest request);

    List<ItemCarrito> getItemsByCarrito(Long carritoId);

    void vaciarCarrito(Long carritoId);

    Carrito getOrCreateCarritoByUsuario(Long usuarioId);
}
