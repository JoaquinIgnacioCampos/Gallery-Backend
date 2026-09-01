package com.uade.tpo.grupo11.gallery.controllers.Tamanio_Lienzo;


import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.services.MensajeService;
import com.uade.tpo.grupo11.gallery.services.TamanioLienzoService;
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
    public ResponseEntity<List<TamanioLienzo>> obtenerTodos() {
        return ResponseEntity.ok(tamanioLienzoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TamanioLienzo> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tamanioLienzoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TamanioLienzo> crearTamanio(@RequestBody TamanioLienzoRequest request) {
        TamanioLienzo nuevoTamanio = tamanioLienzoService.crearTamanio(request);
        return ResponseEntity
                .created(URI.create("/api/tamanios-lienzo/" + nuevoTamanio.getId()))
                .body(nuevoTamanio);
    }
}

