package com.uade.tpo.grupo11.gallery.controllers.tamaniolienzo;


import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.services.tamaniolienzo.TamanioLienzoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tamanios-lienzo")
public class TamanioLienzoController {

    @Autowired
    private TamanioLienzoService tamanioLienzoService;

    @GetMapping
    public ResponseEntity<List<TamanioLienzo>> getTamanioLienzos() {
        return ResponseEntity.ok(tamanioLienzoService.getTamanioLienzos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TamanioLienzo> getTamanioLienzoById(@PathVariable Long id) {
        return ResponseEntity.ok(tamanioLienzoService.getTamanioLienzoById(id));
    }

    @PostMapping
    public ResponseEntity<TamanioLienzo> createTamanioLienzo(@RequestBody TamanioLienzoRequest request) {
        TamanioLienzo nuevoTamanio = tamanioLienzoService.createTamanioLienzo(request);
        return ResponseEntity
                .created(URI.create("/api/tamanios-lienzo/" + nuevoTamanio.getId()))
                .body(nuevoTamanio);
    }
}
