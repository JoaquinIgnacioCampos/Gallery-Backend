package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import com.uade.tpo.grupo11.gallery.services.itemcarrito.ItemCarritoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items-carrito")
public class ItemCarritoController {

    @Autowired
    private ItemCarritoService itemCarritoService;


    // GET - Obtener todos los items
    @GetMapping
    public List<ItemCarritoResponse> getItemsCarrito() {

        return itemCarritoService.getItemsCarrito().stream()
                .map(ItemCarritoResponse::fromEntity)
                .toList();
    }


    // GET - Obtener un item por ID
    @GetMapping("/{itemId}")
    public ItemCarritoResponse getItemCarritoById(
            @PathVariable Long itemId) {

        return ItemCarritoResponse.fromEntity(itemCarritoService.getItemCarritoById(itemId));
    }


    // POST - Crear item
    @PostMapping
    public ItemCarritoResponse createItemCarrito(
            @Valid @RequestBody ItemCarritoRequest request) {

        return ItemCarritoResponse.fromEntity(itemCarritoService.createItemCarrito(request));
    }


    // PUT - Modificar item
    @PutMapping("/{itemId}")
    public ItemCarritoResponse updateItemCarrito(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemCarritoRequest request) {

        return ItemCarritoResponse.fromEntity(itemCarritoService.updateItemCarrito(
                itemId,
                request
        ));
    }


    // DELETE - Eliminar item
    @DeleteMapping("/{itemId}")
    public void deleteItemCarrito(
            @PathVariable Long itemId) {

        itemCarritoService.deleteItemCarrito(itemId);
    }
}
