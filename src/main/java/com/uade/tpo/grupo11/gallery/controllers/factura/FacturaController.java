package com.uade.tpo.grupo11.gallery.controllers.factura;

import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.factura.FacturaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de las facturas y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;


    // GET - Obtener todas las facturas
    @GetMapping
    public ResponseEntity<List<FacturaResponse>> getFacturas(@AuthenticationPrincipal Usuario usuarioLogueado) {
        List<FacturaResponse> result = facturaService.getFacturas(usuarioLogueado).stream()
                .map(FacturaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }


    // GET - Obtener factura por ID
    @GetMapping("/{facturaId}")
    public ResponseEntity<FacturaResponse> getFacturaById(
            @PathVariable Long facturaId,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(FacturaResponse.fromEntity(
                facturaService.getFacturaById(facturaId, usuarioLogueado)));
    }

    //GET - Obtiene la factura por una compra especifica (el id de la compra)
    @GetMapping("/compra/{compraId}")
    public ResponseEntity<List<FacturaResponse>> getFacturasByCompra(
            @PathVariable Long compraId,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<FacturaResponse> result = facturaService.getFacturasByCompra(compraId, usuarioLogueado).stream()
                .map(FacturaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // POST - Crear factura
    @PostMapping
    public ResponseEntity<FacturaResponse> createFactura(@Valid @RequestBody FacturaRequest request) {
        Factura result = facturaService.createFactura(request);
        return ResponseEntity.created(URI.create("/api/facturas/" + result.getId()))
                .body(FacturaResponse.fromEntity(result));
    }


    // PUT - Modificar factura
    @PutMapping("/{facturaId}")
    public ResponseEntity<FacturaResponse> updateFactura(
            @PathVariable Long facturaId,
            @Valid @RequestBody FacturaRequest request) {
        return ResponseEntity.ok(FacturaResponse.fromEntity(
                facturaService.updateFactura(facturaId, request)));
    }


    // DELETE - Eliminar factura
    @DeleteMapping("/{facturaId}")
    public ResponseEntity<Void> deleteFactura(@PathVariable Long facturaId) {
        facturaService.deleteFactura(facturaId);
        return ResponseEntity.noContent().build();
    }
}
