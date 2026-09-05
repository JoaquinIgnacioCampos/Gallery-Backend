package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.services.perfilartista.PerfilArtistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artistas")
public class PerfilArtistaController {

    @Autowired
    private PerfilArtistaService perfilArtistaService;

    @GetMapping
    public ResponseEntity<List<PerfilArtistaResponse>> getPerfilArtistas() {
        List<PerfilArtistaResponse> perfiles = perfilArtistaService.getPerfilArtistas().stream()
                .map(PerfilArtistaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(perfiles);
    }

    @GetMapping("/{perfilArtistaId}")
    public ResponseEntity<PerfilArtistaResponse> getPerfilArtistaById(@PathVariable Long perfilArtistaId) {
        PerfilArtista perfilArtista = perfilArtistaService.getPerfilArtistaById(perfilArtistaId);
        return ResponseEntity.ok(PerfilArtistaResponse.fromEntity(perfilArtista));
    }

    @PatchMapping("/{perfilArtistaId}")
    public ResponseEntity<PerfilArtistaResponse> updatePerfilArtista(
            @PathVariable Long perfilArtistaId,
            @RequestBody PerfilArtistaUpdateRequest request
    ) {
        PerfilArtista perfilArtista = perfilArtistaService.updatePerfilArtista(perfilArtistaId, request);
        return ResponseEntity.ok(PerfilArtistaResponse.fromEntity(perfilArtista));
    }

    @GetMapping("/{perfilArtistaId}/obras")
    public ResponseEntity<List<PerfilArtistaObraResponse>> getObrasByPerfilArtista(
            @PathVariable Long perfilArtistaId
    ) {
        List<PerfilArtistaObraResponse> obras = perfilArtistaService.getObrasByPerfilArtista(perfilArtistaId).stream()
                .map(PerfilArtistaObraResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(obras);
    }
}
