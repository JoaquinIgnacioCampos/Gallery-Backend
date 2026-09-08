package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.obra.ObraService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.Parameter;

// Recibe las peticiones HTTP de las obras y devuelve la respuesta con su codigo. La logica vive en el service.
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


    // Busca la obra por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @GetMapping("/{obraId}")
    public ResponseEntity<ObraResponse> getObraById(@PathVariable Long obraId) {
        return ResponseEntity.ok(ObraResponse.fromEntity(servicioObra.getObraById(obraId)));
    }


    // Crea la obra con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @PostMapping
    public ResponseEntity<ObraResponse> createObra(
            @Valid @RequestBody ObraRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ObraResponse.fromEntity(servicioObra.createObra(request, usuarioLogueado)));
    }


    // Actualiza la obra: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @PutMapping("/{obraId}")
    public ResponseEntity<ObraResponse> updateObra(
            @PathVariable Long obraId,
            @Valid @RequestBody ObraRequest request) {

        return ResponseEntity.ok(ObraResponse.fromEntity(servicioObra.updateObra(obraId, request)));
    }


    // Elimina la obra de la base.
    @DeleteMapping("/{obraId}")
    public ResponseEntity<Void> deleteObra(@PathVariable Long obraId) {
        servicioObra.deleteObra(obraId);
        return ResponseEntity.noContent().build();
    }
}
