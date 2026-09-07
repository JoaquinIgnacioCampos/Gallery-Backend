package com.uade.tpo.grupo11.gallery.controllers.carrito;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoResponse;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;

import java.util.List;

// Recibe las peticiones HTTP de los carritos y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/carritos")
public class    CarritoController {

    @Autowired
    private CarritoService carritoService;


    // GET - Obtener todos los carritos
    @GetMapping
    public List<CarritoResponse> getCarritos() {

        return carritoService.getCarritos().stream()
                .map(CarritoResponse::fromEntity)
                .toList();
    }


    // GET - Obtener carrito por ID
    @GetMapping("/{carritoId}")
    public CarritoResponse getCarritoById(
            @PathVariable Long carritoId) {

        return CarritoResponse.fromEntity(carritoService.getCarritoById(carritoId));
    }


    // POST - Crear carrito
    @PostMapping
    public CarritoResponse createCarrito(
            @Valid @RequestBody CarritoRequest request) {

        return CarritoResponse.fromEntity(carritoService.createCarrito(request));
    }


    // PUT - Modificar carrito
    @PutMapping("/{carritoId}")
    public CarritoResponse updateCarrito(
            @PathVariable Long carritoId,
            @Valid @RequestBody CarritoRequest request) {

        return CarritoResponse.fromEntity(carritoService.updateCarrito(
                carritoId,
                request
        ));
    }


    // Devuelve las lineas del carrito: que variante, cuantas unidades y con que marco.
    @GetMapping("/{carritoId}/items")
    public List<ItemCarritoResponse> getItemsByCarrito(
            @PathVariable Long carritoId) {

        return carritoService.getItemsByCarrito(carritoId).stream()
                .map(ItemCarritoResponse::fromEntity)
                .toList();
    }


    // Saca todos los items del carrito sin borrar el carrito.
    @DeleteMapping("/{carritoId}/items")
    public void vaciarCarrito(
            @PathVariable Long carritoId) {

        carritoService.vaciarCarrito(carritoId);
    }
}
