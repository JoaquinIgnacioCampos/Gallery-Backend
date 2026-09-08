package com.uade.tpo.grupo11.gallery.controllers.itemfactura;

import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.services.itemfactura.ItemFacturaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los items de la factura y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/items-factura")
public class ItemFacturaController {

    @Autowired
    private ItemFacturaService itemFacturaService;

    // Busca el item de la factura por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @GetMapping("/{id}")
    public ResponseEntity<ItemFacturaResponse> getItemFacturaById(@PathVariable Long id) {
        return ResponseEntity.ok(ItemFacturaResponse.fromEntity(itemFacturaService.getItemFacturaById(id)));
    }

    // Devuelve los items de la factura de la factura.
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<ItemFacturaResponse>> getItemFacturasByFactura(@PathVariable Long facturaId) {
        List<ItemFacturaResponse> result = itemFacturaService.getItemFacturasByFactura(facturaId).stream()
                .map(ItemFacturaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // Crea el item de la factura con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @PostMapping
    public ResponseEntity<ItemFacturaResponse> createItemFactura(@Valid @RequestBody ItemFacturaRequest request) {
        ItemFactura result = itemFacturaService.createItemFactura(request);
        return ResponseEntity.created(URI.create("/api/items-factura/" + result.getId()))
                .body(ItemFacturaResponse.fromEntity(result));
    }
}
