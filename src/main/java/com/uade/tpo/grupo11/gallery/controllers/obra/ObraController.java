package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.services.obra.ObraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obras")
public class ObraController {

    private final ObraService servicioObra;

    // Inyeccion por constructor: el campo queda final y se puede testear sin Spring.
    public ObraController(ObraService servicioObra) {
        this.servicioObra = servicioObra;
    }


    // GET /obras           -> todas
    // GET /obras?artistaId=1 -> las de un artista
    @GetMapping
    public ResponseEntity<List<ObraResponse>> getObras(
            @RequestParam(required = false) Long artistaId) {

        List<ObraResponse> result = (artistaId != null
                ? servicioObra.getObrasByArtista(artistaId)
                : servicioObra.getObras())
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
