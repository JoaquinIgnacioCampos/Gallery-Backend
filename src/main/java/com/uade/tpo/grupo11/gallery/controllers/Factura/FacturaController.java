package com.uade.tpo.grupo11.gallery.controllers.factura;

import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.services.factura.FacturaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;


    // GET - Obtener todas las facturas
    @GetMapping
    public List<Factura> getFacturas() {

        return facturaService.getFacturas();
    }


    // GET - Obtener factura por ID
    @GetMapping("/{facturaId}")
    public Factura getFacturaById(
            @PathVariable Long facturaId) {

        return facturaService.getFacturaById(facturaId);
    }


    // POST - Crear factura
    @PostMapping
    public Factura createFactura(
            @RequestBody FacturaRequest request) {

        return facturaService.createFactura(request);
    }


    // PUT - Modificar factura
    @PutMapping("/{facturaId}")
    public Factura updateFactura(
            @PathVariable Long facturaId,
            @RequestBody FacturaRequest request) {

        return facturaService.updateFactura(
                facturaId,
                request
        );
    }


    // DELETE - Eliminar factura
    @DeleteMapping("/{facturaId}")
    public void deleteFactura(
            @PathVariable Long facturaId) {

        facturaService.deleteFactura(facturaId);
    }
}
