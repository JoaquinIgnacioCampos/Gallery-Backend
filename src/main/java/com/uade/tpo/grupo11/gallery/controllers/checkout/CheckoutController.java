package com.uade.tpo.grupo11.gallery.controllers.checkout;

import com.uade.tpo.grupo11.gallery.controllers.compra.CompraResponse;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.checkout.CheckoutService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Recibe las peticiones HTTP de checkout y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    // Convierte el carrito del usuario logueado en una compra con sus facturas. Devuelve 201.
    // El usuario sale del token y no de un parametro: antes cualquiera podia comprar con el carrito de otro.
    @PostMapping
    public ResponseEntity<CompraResponse> checkout(@AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CompraResponse.fromEntity(checkoutService.checkout(usuarioLogueado.getId())));
    }
}
