package com.uade.tpo.grupo11.gallery.controllers.carrito;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoResponse;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;

import java.util.List;

@RestController
@RequestMapping("/api/carritos")
public class    CarritoController {

    @Autowired
    private CarritoService carritoService;


    // GET - Obtener todos los carritos
    @GetMapping
    public List<CarritoResponse> getCarritos(@AuthenticationPrincipal Usuario usuarioActual) {

        return carritoService.getCarritos(usuarioActual).stream()
                .map(CarritoResponse::fromEntity)
                .toList();
    }


    // GET - Obtener carrito por ID
    @GetMapping("/{carritoId}")
    public CarritoResponse getCarritoById(
            @PathVariable Long carritoId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return CarritoResponse.fromEntity(carritoService.getCarritoById(carritoId, usuarioActual));
    }


    // POST - Crear carrito
    @PostMapping
    public CarritoResponse createCarrito(
            @Valid @RequestBody CarritoRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return CarritoResponse.fromEntity(carritoService.createCarrito(request, usuarioActual));
    }


    // PUT - Modificar carrito
    @PutMapping("/{carritoId}")
    public CarritoResponse updateCarrito(
            @PathVariable Long carritoId,
            @Valid @RequestBody CarritoRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return CarritoResponse.fromEntity(carritoService.updateCarrito(
                carritoId,
                request,
                usuarioActual
        ));
    }


    @GetMapping("/{carritoId}/items")
    public List<ItemCarritoResponse> getItemsByCarrito(
            @PathVariable Long carritoId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return carritoService.getItemsByCarrito(carritoId, usuarioActual).stream()
                .map(ItemCarritoResponse::fromEntity)
                .toList();
    }


    @DeleteMapping("/{carritoId}/items")
    public void vaciarCarrito(
            @PathVariable Long carritoId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        carritoService.vaciarCarrito(carritoId, usuarioActual);
    }
}
