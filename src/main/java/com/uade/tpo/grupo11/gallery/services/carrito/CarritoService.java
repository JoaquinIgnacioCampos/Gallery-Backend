package com.uade.tpo.grupo11.gallery.services.carrito;

import java.util.List;

import com.uade.tpo.grupo11.gallery.controllers.Carrito.CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;

public interface CarritoService {

    List<Carrito> getCarritos();

    Carrito getCarritoById(Long carritoId);

    Carrito createCarrito(CarritoRequest request);

    Carrito updateCarrito(Long carritoId, CarritoRequest request);

    void deleteCarrito(Long carritoId);
}
