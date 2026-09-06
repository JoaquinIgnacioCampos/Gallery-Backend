package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.entities.Obra;
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
    public ResponseEntity<List<Obra>> getObras(
            @RequestParam(required = false) Long artistaId) {

        if (artistaId != null) {
            return ResponseEntity.ok(servicioObra.getObrasByArtista(artistaId));
        }

        return ResponseEntity.ok(servicioObra.getObras());
    }


    @GetMapping("/{obraId}")
    public ResponseEntity<Obra> getObraById(@PathVariable Long obraId) {
        return ResponseEntity.ok(servicioObra.getObraById(obraId));
    }


    @PostMapping
    public ResponseEntity<Obra> createObra(@Valid @RequestBody ObraRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(servicioObra.createObra(request));
    }


    @PutMapping("/{obraId}")
    public ResponseEntity<Obra> updateObra(
            @PathVariable Long obraId,
            @Valid @RequestBody ObraRequest request) {

        return ResponseEntity.ok(servicioObra.updateObra(obraId, request));
    }


    @DeleteMapping("/{obraId}")
    public ResponseEntity<Void> deleteObra(@PathVariable Long obraId) {
        servicioObra.deleteObra(obraId);
        return ResponseEntity.noContent().build();
    }
}
