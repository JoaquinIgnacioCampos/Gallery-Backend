package com.uade.tpo.grupo11.gallery.controllers.estilo;

import com.uade.tpo.grupo11.gallery.services.estilo.EstiloService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<EstiloResponse>> getTodosLosEstilos() {
        List<EstiloResponse> result = estiloService.obtenerTodos().stream()
                .map(EstiloResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstiloResponse> getEstiloPorId(@PathVariable Long id) {
        return ResponseEntity.ok(EstiloResponse.fromEntity(estiloService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<EstiloResponse> crearEstilo(@Valid @RequestBody EstiloRequest request) {
        return ResponseEntity.ok(EstiloResponse.fromEntity(estiloService.crearEstilo(request)));
    }
}