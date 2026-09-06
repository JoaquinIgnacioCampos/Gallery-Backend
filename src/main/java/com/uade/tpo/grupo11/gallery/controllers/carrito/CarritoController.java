package com.uade.tpo.grupo11.gallery.controllers.carrito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoResponse;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;

import java.util.List;

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
            @RequestBody CarritoRequest request) {

        return CarritoResponse.fromEntity(carritoService.createCarrito(request));
    }


    // PUT - Modificar carrito
    @PutMapping("/{carritoId}")
    public CarritoResponse updateCarrito(
            @PathVariable Long carritoId,
            @RequestBody CarritoRequest request) {

        return CarritoResponse.fromEntity(carritoService.updateCarrito(
                carritoId,
                request
        ));
    }


    @GetMapping("/{carritoId}/items")
    public List<ItemCarritoResponse> getItemsByCarrito(
            @PathVariable Long carritoId) {

        return carritoService.getItemsByCarrito(carritoId).stream()
                .map(ItemCarritoResponse::fromEntity)
                .toList();
    }


    @DeleteMapping("/{carritoId}/items")
    public void vaciarCarrito(
            @PathVariable Long carritoId) {

        carritoService.vaciarCarrito(carritoId);
    }
}
