package com.uade.tpo.grupo11.gallery.controllers.checkout;

import com.uade.tpo.grupo11.gallery.controllers.compra.CompraResponse;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.checkout.CheckoutService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    // POST /api/checkout
    // Convierte el carrito del usuario LOGUEADO en una compra con sus facturas.
    // 201: se creo un recurso nuevo (la compra).
    //
    // El usuario NO llega por parametro. @AuthenticationPrincipal inyecta el usuario
    // que el filtro de JWT dejo registrado al validar el token, asi que la identidad
    // sale de una firma y no de algo que el cliente pueda escribir.
    // Antes recibia ?usuarioId=1 y cualquiera podia comprar con el carrito de otro.
    @PostMapping
    public ResponseEntity<CompraResponse> checkout(@AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CompraResponse.fromEntity(checkoutService.checkout(usuarioLogueado.getId())));
    }
}
