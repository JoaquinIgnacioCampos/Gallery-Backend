package com.uade.tpo.grupo11.gallery.controllers.carrito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;

import java.util.List;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;


    // GET - Obtener todos los carritos
    @GetMapping
    public List<Carrito> getCarritos() {

        return carritoService.getCarritos();
    }


    // GET - Obtener carrito por ID
    @GetMapping("/{carritoId}")
    public Carrito getCarritoById(
            @PathVariable Long carritoId) {

        return carritoService.getCarritoById(carritoId);
    }


    // POST - Crear carrito
    @PostMapping
    public Carrito createCarrito(
            @RequestBody CarritoRequest request) {

        return carritoService.createCarrito(request);
    }


    // PUT - Modificar carrito
    @PutMapping("/{carritoId}")
    public Carrito updateCarrito(
            @PathVariable Long carritoId,
            @RequestBody CarritoRequest request) {

        return carritoService.updateCarrito(
                carritoId,
                request
        );
    }

    // DELETE - Eliminar carrito
    @DeleteMapping("/{carritoId}")
    public void deleteCarrito(
            @PathVariable Long carritoId) {

        carritoService.deleteCarrito(carritoId);
    }
}
