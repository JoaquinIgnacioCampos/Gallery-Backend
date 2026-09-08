package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.itemcarrito.ItemCarritoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recibe las peticiones HTTP de los items del carrito y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/items-carrito")
public class ItemCarritoController {

    @Autowired
    private ItemCarritoService itemCarritoService;


    // GET - Obtener todos los items
    @GetMapping
    public List<ItemCarritoResponse> getItemsCarrito(@AuthenticationPrincipal Usuario usuarioActual) {

        return itemCarritoService.getItemsCarrito(usuarioActual).stream()
                .map(ItemCarritoResponse::fromEntity)
                .toList();
    }


    // GET - Obtener un item por ID
    @GetMapping("/{itemId}")
    public ItemCarritoResponse getItemCarritoById(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return ItemCarritoResponse.fromEntity(itemCarritoService.getItemCarritoById(itemId, usuarioActual));
    }


    // POST - Crear item
    @PostMapping
    public ItemCarritoResponse createItemCarrito(
            @Valid @RequestBody ItemCarritoRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return ItemCarritoResponse.fromEntity(itemCarritoService.createItemCarrito(request, usuarioActual));
    }


    // PUT - Modificar item
    @PutMapping("/{itemId}")
    public ItemCarritoResponse updateItemCarrito(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemCarritoRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) {

        return ItemCarritoResponse.fromEntity(itemCarritoService.updateItemCarrito(
                itemId,
                request,
                usuarioActual
        ));
    }


    // DELETE - Eliminar item
    @DeleteMapping("/{itemId}")
    public void deleteItemCarrito(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Usuario usuarioActual) {

        itemCarritoService.deleteItemCarrito(itemId, usuarioActual);
    }
}
