package com.uade.tpo.grupo11.gallery.controllers.factura;

import com.uade.tpo.grupo11.gallery.services.factura.FacturaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recibe las peticiones HTTP de las facturas y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;


    // GET - Obtener todas las facturas
    @GetMapping
    public List<FacturaResponse> getFacturas() {

        return facturaService.getFacturas().stream()
                .map(FacturaResponse::fromEntity)
                .toList();
    }


    // GET - Obtener factura por ID
    @GetMapping("/{facturaId}")
    public FacturaResponse getFacturaById(
            @PathVariable Long facturaId) {

        return FacturaResponse.fromEntity(facturaService.getFacturaById(facturaId));
    }


    // POST - Crear factura
    @PostMapping
    public FacturaResponse createFactura(
            @Valid @RequestBody FacturaRequest request) {

        return FacturaResponse.fromEntity(facturaService.createFactura(request));
    }


    // PUT - Modificar factura
    @PutMapping("/{facturaId}")
    public FacturaResponse updateFactura(
            @PathVariable Long facturaId,
            @Valid @RequestBody FacturaRequest request) {

        return FacturaResponse.fromEntity(facturaService.updateFactura(
                facturaId,
                request
        ));
    }


    // DELETE - Eliminar factura
    @DeleteMapping("/{facturaId}")
    public void deleteFactura(
            @PathVariable Long facturaId) {

        facturaService.deleteFactura(facturaId);
    }
}
