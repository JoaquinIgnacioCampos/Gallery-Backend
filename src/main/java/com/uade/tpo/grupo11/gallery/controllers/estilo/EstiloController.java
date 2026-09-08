package com.uade.tpo.grupo11.gallery.controllers.estilo;

import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.services.estilo.EstiloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los estilos y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/estilos")
public class EstiloController {

    @Autowired
    private EstiloService estiloService;

    // Devuelve los estilos.
    @GetMapping
    public ResponseEntity<List<EstiloResponse>> getTodosLosEstilos() {
        List<EstiloResponse> result = estiloService.obtenerTodos().stream()
                .map(EstiloResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // Busca el estilo por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @GetMapping("/{id}")
    public ResponseEntity<EstiloResponse> getEstiloPorId(@PathVariable Long id) {
        return ResponseEntity.ok(EstiloResponse.fromEntity(estiloService.obtenerPorId(id)));
    }

    // Crea el estilo con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @PostMapping
    public ResponseEntity<EstiloResponse> crearEstilo(@Valid @RequestBody EstiloRequest request) {
        Estilo result = estiloService.crearEstilo(request);
        return ResponseEntity.created(URI.create("/api/estilos/" + result.getId()))
                .body(EstiloResponse.fromEntity(result));
    }
}