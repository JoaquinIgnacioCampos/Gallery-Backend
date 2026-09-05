package com.uade.tpo.grupo11.gallery.controllers.compra;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.services.compra.CompraService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;


    // GET - Obtener todas las compras
    @GetMapping
    public List<Compra> getCompras() {

        return compraService.getCompras();
    }


    // GET - Obtener una compra por ID
    @GetMapping("/{compraId}")
    public Compra getCompraById(
            @PathVariable Long compraId) {

        return compraService.getCompraById(compraId);
    }


    // POST - Crear compra
    @PostMapping
    public Compra createCompra(
            @RequestBody CompraRequest request) {

        return compraService.createCompra(request);
    }


    // PUT - Modificar compra
    @PutMapping("/{compraId}")
    public Compra updateCompra(
            @PathVariable Long compraId,
            @RequestBody CompraRequest request) {

        return compraService.updateCompra(
                compraId,
                request
        );
    }


    // DELETE - Eliminar compra
    @DeleteMapping("/{compraId}")
    public void deleteCompra(
            @PathVariable Long compraId) {

        compraService.deleteCompra(compraId);
    }
}