package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.services.variante.VarianteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CONTROLLER de Variante. Misma estructura que ObraController: recibe HTTP, delega en el
// Service y devuelve el codigo que corresponde. Devuelve siempre Response, nunca la entity.
@RestController
@RequestMapping("/api/variantes")
public class VarianteController {

    // Inyeccion por constructor: el campo queda final y se puede testear sin Spring.
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


    // POST crea: 201 CREATED. @Valid revisa el Request antes de entrar al metodo.
    @PostMapping
    public ResponseEntity<VarianteResponse> createVariante(@Valid @RequestBody VarianteRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VarianteResponse.fromEntity(servicioVariante.createVariante(request)));
    }


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


    // DELETE devuelve 204 NO CONTENT: salio bien y no hay cuerpo que devolver.
    @DeleteMapping("/{varianteId}")
    public ResponseEntity<Void> deleteVariante(@PathVariable Long varianteId) {
        servicioVariante.deleteVariante(varianteId);
        return ResponseEntity.noContent().build();
    }
}
