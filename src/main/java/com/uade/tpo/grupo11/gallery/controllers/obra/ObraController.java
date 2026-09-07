package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.services.obra.ObraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/obras")
public class ObraController {

    private final ObraService servicioObra;

    // Inyeccion por constructor: el campo queda final y se puede testear sin Spring.
    public ObraController(ObraService servicioObra) {
        this.servicioObra = servicioObra;
    }


    // GET /api/obras: filtros opcionales y combinables, con limites inclusivos.
    @GetMapping
    public ResponseEntity<List<ObraResponse>> getObras(
            @RequestParam(required = false) Long artistaId,
            @Parameter(description = "Categoria de la obra: ID del estilo. Opcional.")
            @RequestParam(required = false) Long estiloId,
            @Parameter(description = "Precio base minimo de una variante, inclusive; sin marco ni descuentos.")
            @RequestParam(required = false) BigDecimal precioMin,
            @Parameter(description = "Precio base maximo de la misma variante, inclusive; sin marco ni descuentos.")
            @RequestParam(required = false) BigDecimal precioMax) {

        List<ObraResponse> result = servicioObra.buscarConFiltros(artistaId, estiloId, precioMin, precioMax)
                .stream()
                .map(ObraResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }


    @GetMapping("/{obraId}")
    public ResponseEntity<ObraResponse> getObraById(@PathVariable Long obraId) {
        return ResponseEntity.ok(ObraResponse.fromEntity(servicioObra.getObraById(obraId)));
    }


    @PostMapping
    public ResponseEntity<ObraResponse> createObra(@Valid @RequestBody ObraRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ObraResponse.fromEntity(servicioObra.createObra(request)));
    }


    @PutMapping("/{obraId}")
    public ResponseEntity<ObraResponse> updateObra(
            @PathVariable Long obraId,
            @Valid @RequestBody ObraRequest request) {

        return ResponseEntity.ok(ObraResponse.fromEntity(servicioObra.updateObra(obraId, request)));
    }


    @DeleteMapping("/{obraId}")
    public ResponseEntity<Void> deleteObra(@PathVariable Long obraId) {
        servicioObra.deleteObra(obraId);
        return ResponseEntity.noContent().build();
    }
}
