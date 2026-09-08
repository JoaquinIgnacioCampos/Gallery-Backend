package com.uade.tpo.grupo11.gallery.controllers.compra;

import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaResponse;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.services.compra.CompraService;
import com.uade.tpo.grupo11.gallery.services.factura.FacturaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de las compras y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private FacturaService facturaService;


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
    public ResponseEntity<CompraResponse> createCompra(
            @Valid @RequestBody CompraRequest request) {

        Compra result = compraService.createCompra(request);
        return ResponseEntity.created(URI.create("/api/compras/" + result.getId()))
                .body(CompraResponse.fromEntity(result));
    }


    // PUT - Modificar compra
    @PutMapping("/{compraId}")
    public CompraResponse updateCompra(
            @PathVariable Long compraId,
            @Valid @RequestBody CompraRequest request) {

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


    // Devuelve las facturas de la compra.
    @GetMapping("/{compraId}/facturas")
    public List<FacturaResponse> getFacturasByCompra(
            @PathVariable Long compraId) {

        return facturaService.getFacturasByCompra(compraId).stream()
                .map(FacturaResponse::fromEntity)
                .toList();
    }
}