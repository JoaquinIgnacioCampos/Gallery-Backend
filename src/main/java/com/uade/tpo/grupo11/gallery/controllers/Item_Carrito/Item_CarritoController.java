package com.uade.tpo.grupo11.gallery.controllers.Item_Carrito;

import com.uade.tpo.grupo11.gallery.entities.Item_Carrito;
import com.uade.tpo.grupo11.gallery.services.itemCarrito.Item_CarritoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items-carrito")
public class Item_CarritoController {

    @Autowired
    private Item_CarritoService itemCarritoService;


    // GET - Obtener todos los items
    @GetMapping
    public List<Item_Carrito> getItemsCarrito() {

        return itemCarritoService.getItemsCarrito();
    }


    // GET - Obtener un item por ID
    @GetMapping("/{itemId}")
    public Item_Carrito getItemCarritoById(
            @PathVariable Long itemId) {

        return itemCarritoService.getItemCarritoById(itemId);
    }


    // POST - Crear item
    @PostMapping
    public Item_Carrito createItemCarrito(
            @RequestBody Item_CarritoRequest request) {

        return itemCarritoService.createItemCarrito(request);
    }


    // PUT - Modificar item
    @PutMapping("/{itemId}")
    public Item_Carrito updateItemCarrito(
            @PathVariable Long itemId,
            @RequestBody Item_CarritoRequest request) {

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
