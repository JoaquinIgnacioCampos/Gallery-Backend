package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.services.variante.VarianteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variantes")
public class VarianteController {

    private final VarianteService servicioVariante;

    public VarianteController(VarianteService servicioVariante) {
        this.servicioVariante = servicioVariante;
    }


    // GET /variantes          -> todas
    // GET /variantes?obraId=1 -> las de una obra
    @GetMapping
    public ResponseEntity<List<Variante>> getVariantes(
            @RequestParam(required = false) Long obraId) {

        if (obraId != null) {
            return ResponseEntity.ok(servicioVariante.getVariantesByObra(obraId));
        }

        return ResponseEntity.ok(servicioVariante.getVariantes());
    }


    // El nombre entre llaves y el del parametro tienen que coincidir letra por letra.
    @GetMapping("/{varianteId}")
    public ResponseEntity<Variante> getVarianteById(@PathVariable Long varianteId) {
        return ResponseEntity.ok(servicioVariante.getVarianteById(varianteId));
    }


    @PostMapping
    public ResponseEntity<Variante> createVariante(@RequestBody VarianteRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(servicioVariante.createVariante(request));
    }


    @PutMapping("/{varianteId}")
    public ResponseEntity<Variante> updateVariante(
            @PathVariable Long varianteId,
            @RequestBody VarianteRequest request) {

        return ResponseEntity.ok(servicioVariante.updateVariante(varianteId, request));
    }


    // PATCH y no PUT: se modifica un solo campo, no se reemplaza la variante entera.
    @PatchMapping("/{varianteId}/stock")
    public ResponseEntity<Variante> actualizarStock(
            @PathVariable Long varianteId,
            @RequestBody Integer nuevoStock) {

        return ResponseEntity.ok(servicioVariante.actualizarStock(varianteId, nuevoStock));
    }


    @DeleteMapping("/{varianteId}")
    public ResponseEntity<Void> deleteVariante(@PathVariable Long varianteId) {
        servicioVariante.deleteVariante(varianteId);
        return ResponseEntity.noContent().build();
    }
}
