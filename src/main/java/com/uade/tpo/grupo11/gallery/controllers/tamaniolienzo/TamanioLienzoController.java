package com.uade.tpo.grupo11.gallery.controllers.tamaniolienzo;


import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.services.tamaniolienzo.TamanioLienzoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los tamanios de lienzo y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/tamanios-lienzo")
public class TamanioLienzoController {

    @Autowired
    private TamanioLienzoService tamanioLienzoService;

    // Devuelve los tamanios de lienzo.
    @GetMapping
    public ResponseEntity<List<TamanioLienzo>> getTamanioLienzos() {
        return ResponseEntity.ok(tamanioLienzoService.getTamanioLienzos());
    }

    // Busca el tamanio de lienzo por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @GetMapping("/{id}")
    public ResponseEntity<TamanioLienzo> getTamanioLienzoById(@PathVariable Long id) {
        return ResponseEntity.ok(tamanioLienzoService.getTamanioLienzoById(id));
    }

    // Crea el tamanio de lienzo con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @PostMapping
    public ResponseEntity<TamanioLienzo> createTamanioLienzo(@Valid @RequestBody TamanioLienzoRequest request) {
        TamanioLienzo nuevoTamanio = tamanioLienzoService.createTamanioLienzo(request);
        return ResponseEntity
                .created(URI.create("/api/tamanios-lienzo/" + nuevoTamanio.getId()))
                .body(nuevoTamanio);
    }
}
