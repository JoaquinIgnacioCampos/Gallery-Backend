package com.uade.tpo.grupo11.gallery.controllers.ItemCarrito;

import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.services.ItemCarritoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items-carrito")
public class ItemCarritoController {

    @Autowired
    private ItemCarritoService itemCarritoService;


    // GET - Obtener todos los items
    @GetMapping
    public List<ItemCarrito> getItemsCarrito() {

        return itemCarritoService.getItemsCarrito();
    }


    // GET - Obtener un item por ID
    @GetMapping("/{itemId}")
    public ItemCarrito getItemCarritoById(
            @PathVariable Long itemId) {

        return itemCarritoService.getItemCarritoById(itemId);
    }


    // POST - Crear item
    @PostMapping
    public ItemCarrito createItemCarrito(
            @RequestBody ItemCarritoRequest request) {

        return itemCarritoService.createItemCarrito(request);
    }


    // PUT - Modificar item
    @PutMapping("/{itemId}")
    public ItemCarrito updateItemCarrito(
            @PathVariable Long itemId,
            @RequestBody ItemCarritoRequest request) {

        return itemCarritoService.updateItemCarrito(
                itemId,
                request
        );
    }


    // DELETE - Eliminar item
    @DeleteMapping("/{itemId}")
    public void deleteItemCarrito(
            @PathVariable Long itemId) {

        itemCarritoService.deleteItemCarrito(itemId);
    }
}
