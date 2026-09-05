package com.uade.tpo.grupo11.gallery.controllers.itemfactura;

import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.services.itemfactura.ItemFacturaService;
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
    public ResponseEntity<ItemFactura> getItemFacturaById(@PathVariable Long id) {
        return ResponseEntity.ok(itemFacturaService.getItemFacturaById(id));
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<ItemFactura>> getItemFacturasByFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(itemFacturaService.getItemFacturasByFactura(facturaId));
    }

    @PostMapping
    public ResponseEntity<ItemFactura> createItemFactura(@RequestBody ItemFacturaRequest request) {
        return ResponseEntity.ok(itemFacturaService.createItemFactura(request));
    }
}
