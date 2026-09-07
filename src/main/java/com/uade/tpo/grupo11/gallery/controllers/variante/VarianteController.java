package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.services.variante.VarianteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recibe las peticiones HTTP de las variantes y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/variantes")
public class VarianteController {

    private final VarianteService servicioVariante;

    public VarianteController(VarianteService servicioVariante) {
        this.servicioVariante = servicioVariante;
    }


    // GET /variantes          -> todas
    // GET /variantes?obraId=1 -> las de una obra
    @GetMapping
    public ResponseEntity<List<VarianteResponse>> getVariantes(
            @RequestParam(required = false) Long obraId) {

        List<VarianteResponse> result = (obraId != null
                ? servicioVariante.getVariantesByObra(obraId)
                : servicioVariante.getVariantes())
                .stream()
                .map(VarianteResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }


    // El nombre entre llaves y el del parametro tienen que coincidir letra por letra.
    @GetMapping("/{varianteId}")
    public ResponseEntity<VarianteResponse> getVarianteById(@PathVariable Long varianteId) {
        return ResponseEntity.ok(VarianteResponse.fromEntity(servicioVariante.getVarianteById(varianteId)));
    }


    // Crea la variante con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @PostMapping
    public ResponseEntity<VarianteResponse> createVariante(@Valid @RequestBody VarianteRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VarianteResponse.fromEntity(servicioVariante.createVariante(request)));
    }


    // Actualiza la variante: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @PutMapping("/{varianteId}")
    public ResponseEntity<VarianteResponse> updateVariante(
            @PathVariable Long varianteId,
            @Valid @RequestBody VarianteRequest request) {

        return ResponseEntity.ok(VarianteResponse.fromEntity(servicioVariante.updateVariante(varianteId, request)));
    }


    // PATCH y no PUT: se modifica un solo campo, no se reemplaza la variante entera.
    @PatchMapping("/{varianteId}/stock")
    public ResponseEntity<VarianteResponse> actualizarStock(
            @PathVariable Long varianteId,
            @RequestBody Integer nuevoStock) {

        return ResponseEntity.ok(VarianteResponse.fromEntity(servicioVariante.actualizarStock(varianteId, nuevoStock)));
    }


    // Elimina la variante de la base.
    @DeleteMapping("/{varianteId}")
    public ResponseEntity<Void> deleteVariante(@PathVariable Long varianteId) {
        servicioVariante.deleteVariante(varianteId);
        return ResponseEntity.noContent().build();
    }
}
