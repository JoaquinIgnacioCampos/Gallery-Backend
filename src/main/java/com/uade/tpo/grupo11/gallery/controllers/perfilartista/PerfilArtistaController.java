package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.services.perfilartista.PerfilArtistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recibe las peticiones HTTP de los perfiles de artista y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/artistas")
public class PerfilArtistaController {

    @Autowired
    private PerfilArtistaService perfilArtistaService;

    // Devuelve los perfiles de artista.
    @GetMapping
    public ResponseEntity<List<PerfilArtistaResponse>> getPerfilArtistas() {
        List<PerfilArtistaResponse> perfiles = perfilArtistaService.getPerfilArtistas().stream()
                .map(PerfilArtistaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(perfiles);
    }

    // Busca el perfil de artista por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @GetMapping("/{perfilArtistaId}")
    public ResponseEntity<PerfilArtistaResponse> getPerfilArtistaById(@PathVariable Long perfilArtistaId) {
        PerfilArtista perfilArtista = perfilArtistaService.getPerfilArtistaById(perfilArtistaId);
        return ResponseEntity.ok(PerfilArtistaResponse.fromEntity(perfilArtista));
    }

    // Actualiza el perfil de artista: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @PatchMapping("/{perfilArtistaId}")
    public ResponseEntity<PerfilArtistaResponse> updatePerfilArtista(
            @PathVariable Long perfilArtistaId,
            @Valid @RequestBody PerfilArtistaUpdateRequest request
    ) {
        PerfilArtista perfilArtista = perfilArtistaService.updatePerfilArtista(perfilArtistaId, request);
        return ResponseEntity.ok(PerfilArtistaResponse.fromEntity(perfilArtista));
    }

    // Devuelve las obras del perfil de artista.
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
