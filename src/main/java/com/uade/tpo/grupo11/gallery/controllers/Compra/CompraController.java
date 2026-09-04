package com.uade.tpo.grupo11.gallery.controllers.Compra;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.services.Compra.CompraService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;


    //CONSTRUCTOR
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    // GET - Obtener todas las compras
    @GetMapping
    public ResponseEntity<List<Compra>> getListCompras() {
        return ResponseEntity.ok(compraService.getListCompras());
    }


    // GET - Obtener una compra por ID
    @GetMapping("/{compraId}")
    public ResponseEntity<Compra> getCompraById(@PathVariable Long compraId) {
        return ResponseEntity.ok(compraService.getCompraById(compraId));
    }

    //POST - crear compra
    @PostMapping
    public ResponseEntity<Compra> crearCompra(@RequestBody Compra compra) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compraService.createCompra(compra));
    }


    // PUT - Modificar compra
    @PutMapping("/{compraId}")
    public ResponseEntity<Compra> updateCompra(
            @PathVariable Long compraId,
            @RequestBody Compra compra) {

        return ResponseEntity.ok(compraService.modificarCompra(compraId,compra);
    }


    // DELETE - Eliminar compra
    @DeleteMapping("/{compraId}")
    public ResponseEntity<Void> eliminarCompra(@PathVariable Long obraId) {
        compraService.eliminarCompra(compraId);
        return ResponseEntity.noContent().build();
    }
}


