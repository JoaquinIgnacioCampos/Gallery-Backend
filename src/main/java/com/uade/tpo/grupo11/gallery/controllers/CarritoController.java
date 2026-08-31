package com.uade.tpo.grupo11.gallery.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.services.CarritoService;

@RestController

@RequestMapping("/carritos")
public class CarritoController {

    @Autowired
    private CarritoService servicioCarrito;

    @GetMapping("/{carritoId}")
    public ResponseEntity<Carrito> getCarritoById(@PathVariable Long carritoId) {
        return ResponseEntity.ok(servicioCarrito.getCarritoById(carritoId)); 
    }

}
