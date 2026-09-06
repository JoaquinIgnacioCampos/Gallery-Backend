package com.uade.tpo.grupo11.gallery.controllers.checkout;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.services.checkout.CheckoutService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    // POST /checkout?usuarioId=1
    // Convierte el carrito del usuario en una compra con sus facturas.
    // 201: se creo un recurso nuevo (la compra).
    @PostMapping
    public ResponseEntity<Compra> checkout(@RequestParam Long usuarioId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(checkoutService.checkout(usuarioId));
    }
}
