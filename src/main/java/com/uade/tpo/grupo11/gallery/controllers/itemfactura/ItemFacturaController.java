package com.uade.tpo.grupo11.gallery.controllers.itemfactura;

import com.uade.tpo.grupo11.gallery.services.itemfactura.ItemFacturaService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ItemFacturaResponse> getItemFacturaById(@PathVariable Long id) {
        return ResponseEntity.ok(ItemFacturaResponse.fromEntity(itemFacturaService.getItemFacturaById(id)));
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<ItemFacturaResponse>> getItemFacturasByFactura(@PathVariable Long facturaId) {
        List<ItemFacturaResponse> result = itemFacturaService.getItemFacturasByFactura(facturaId).stream()
                .map(ItemFacturaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ItemFacturaResponse> createItemFactura(@Valid @RequestBody ItemFacturaRequest request) {
        return ResponseEntity.ok(ItemFacturaResponse.fromEntity(itemFacturaService.createItemFactura(request)));
    }
}
