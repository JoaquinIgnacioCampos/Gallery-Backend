package com.uade.tpo.grupo11.gallery.controllers.ItemFactura;

import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.services.itemFactura.ItemFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items-factura")
public class ItemFacturaController {

    @Autowired
    private ItemFacturaService itemFacturaService;

    @GetMapping("/{id}")
    public ResponseEntity<ItemFactura> getItemFacturaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemFacturaService.obtenerPorId(id));
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<ItemFactura>> getItemsPorFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(itemFacturaService.obtenerPorFactura(facturaId));
    }

    @PostMapping
    public ResponseEntity<ItemFactura> crearItemFactura(@RequestBody ItemFacturaRequest request) {
        return ResponseEntity.ok(itemFacturaService.crearItemFactura(request));
    }
}
