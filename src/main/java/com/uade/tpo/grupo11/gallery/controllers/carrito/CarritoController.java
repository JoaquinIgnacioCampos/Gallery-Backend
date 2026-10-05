package com.uade.tpo.grupo11.gallery.controllers.carrito;

import com.uade.tpo.grupo11.gallery.controllers.paginacion.Paginacion;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<CarritoResponse>> getCarritos(
            @AuthenticationPrincipal Usuario usuarioActual,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(carritoService.getCarritos(usuarioActual, Paginacion.de(page, size))
                .map(CarritoResponse::fromEntity));
    }


    // GET - Obtener carrito por ID
    @GetMapping("/{carritoId}")
    public CarritoResponse getCarritoById(
            @PathVariable Long carritoId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return CarritoResponse.fromEntity(carritoService.getCarritoById(carritoId, usuarioActual));
    }


    // El propio carrito: el id sale del token, no hace falta pasar primero por /api/usuarios/{id}.
    // Se crea solo si todavia no existe, igual que el equivalente en UsuarioController.
    @GetMapping("/me")
    public CarritoResponse getCarritoPropio(@AuthenticationPrincipal Usuario usuarioActual) {
        return CarritoResponse.fromEntity(
                carritoService.getOrCreateCarritoByUsuario(usuarioActual.getId(), usuarioActual));
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


    // Modifica el propio carrito: el id sale del token.
    @PutMapping("/me")
    public CarritoResponse updateCarritoPropio(
            @Valid @RequestBody CarritoRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) {

        Carrito propio = carritoService.getOrCreateCarritoByUsuario(usuarioActual.getId(), usuarioActual);
        return CarritoResponse.fromEntity(carritoService.updateCarrito(
                propio.getId(),
                request,
                usuarioActual
        ));
    }


    // Devuelve las lineas del carrito: que variante, cuantas unidades y con que marco.
    @GetMapping("/{carritoId}/items")
    public ResponseEntity<Page<ItemCarritoResponse>> getItemsByCarrito(
            @PathVariable Long carritoId,
            @AuthenticationPrincipal Usuario usuarioActual,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        return ResponseEntity.ok(carritoService.getItemsByCarrito(carritoId, usuarioActual, Paginacion.de(page, size))
                .map(ItemCarritoResponse::fromEntity));
    }


    // Las lineas del propio carrito: el id sale del token.
    @GetMapping("/me/items")
    public List<ItemCarritoResponse> getItemsByCarritoPropio(@AuthenticationPrincipal Usuario usuarioActual) {
        Carrito propio = carritoService.getOrCreateCarritoByUsuario(usuarioActual.getId(), usuarioActual);
        return carritoService.getItemsByCarrito(propio.getId(), usuarioActual).stream()
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


    // Vacia el propio carrito: el id sale del token.
    @DeleteMapping("/me/items")
    public void vaciarCarritoPropio(@AuthenticationPrincipal Usuario usuarioActual) {
        Carrito propio = carritoService.getOrCreateCarritoByUsuario(usuarioActual.getId(), usuarioActual);
        carritoService.vaciarCarrito(propio.getId(), usuarioActual);
    }
}
