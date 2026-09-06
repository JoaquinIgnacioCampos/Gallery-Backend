package com.uade.tpo.grupo11.gallery.controllers.compra;

import com.uade.tpo.grupo11.gallery.services.compra.CompraService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;


    // GET - Obtener todas las compras
    @GetMapping
    public List<CompraResponse> getCompras() {

        return compraService.getCompras().stream()
                .map(CompraResponse::fromEntity)
                .toList();
    }


    // GET - Obtener una compra por ID
    @GetMapping("/{compraId}")
    public CompraResponse getCompraById(
            @PathVariable Long compraId) {

        return CompraResponse.fromEntity(compraService.getCompraById(compraId));
    }


    // POST - Crear compra
    @PostMapping
    public CompraResponse createCompra(
            @RequestBody CompraRequest request) {

        return CompraResponse.fromEntity(compraService.createCompra(request));
    }


    // PUT - Modificar compra
    @PutMapping("/{compraId}")
    public CompraResponse updateCompra(
            @PathVariable Long compraId,
            @RequestBody CompraRequest request) {

        return CompraResponse.fromEntity(compraService.updateCompra(
                compraId,
                request
        ));
    }


    // DELETE - Eliminar compra
    @DeleteMapping("/{compraId}")
    public void deleteCompra(
            @PathVariable Long compraId) {

        compraService.deleteCompra(compraId);
    }
}