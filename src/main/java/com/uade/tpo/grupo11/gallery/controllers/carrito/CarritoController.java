package com.uade.tpo.grupo11.gallery.controllers.carrito;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoResponse;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los carritos y devuelve la respuesta con su codigo. La logica vive en el service.
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
    public ResponseEntity<CarritoResponse> createCarrito(
            @Valid @RequestBody CarritoRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) {

        Carrito result = carritoService.createCarrito(request, usuarioActual);
        return ResponseEntity.created(URI.create("/api/carritos/" + result.getId()))
                .body(CarritoResponse.fromEntity(result));
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


    // Devuelve las lineas del carrito: que variante, cuantas unidades y con que marco.
    @GetMapping("/{carritoId}/items")
    public List<ItemCarritoResponse> getItemsByCarrito(
            @PathVariable Long carritoId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return carritoService.getItemsByCarrito(carritoId, usuarioActual).stream()
                .map(ItemCarritoResponse::fromEntity)
                .toList();
    }


    // Saca todos los items del carrito sin borrar el carrito.
    @DeleteMapping("/{carritoId}/items")
    public void vaciarCarrito(
            @PathVariable Long carritoId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        carritoService.vaciarCarrito(carritoId, usuarioActual);
    }
}
