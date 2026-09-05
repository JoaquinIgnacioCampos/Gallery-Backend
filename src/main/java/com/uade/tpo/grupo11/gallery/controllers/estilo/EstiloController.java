package com.uade.tpo.grupo11.gallery.controllers.estilo;

import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.services.estilo.EstiloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estilos")
public class EstiloController {

    @Autowired
    private EstiloService estiloService;

    @GetMapping
    public ResponseEntity<List<Estilo>> getTodosLosEstilos() {
        return ResponseEntity.ok(estiloService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estilo> getEstiloPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estiloService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Estilo> crearEstilo(@RequestBody EstiloRequest request) {
        return ResponseEntity.ok(estiloService.crearEstilo(request));
    }
}