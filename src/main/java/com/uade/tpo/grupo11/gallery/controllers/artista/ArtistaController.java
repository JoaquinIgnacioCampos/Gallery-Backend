package com.uade.tpo.grupo11.gallery.controllers.artista;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.services.ArtistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    public ArtistaController() {
    }

    @Autowired
    private ArtistaService artistaService;

    @GetMapping
    public ResponseEntity<List<ArtistaResponse>> listarArtistas() {
        List<ArtistaResponse> artistas = artistaService.listarArtistas().stream()
                .map(ArtistaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(artistas);
    }

    @GetMapping("/{artistaId}")
    public ResponseEntity<ArtistaResponse> getArtistaById(@PathVariable Long artistaId) {
        PerfilArtista artista = artistaService.getArtistaById(artistaId);
        return ResponseEntity.ok(ArtistaResponse.fromEntity(artista));
    }

    @PostMapping
    public ResponseEntity<ArtistaResponse> crearArtista(@RequestBody ArtistaRequest request) {
        PerfilArtista artista = artistaService.crearArtista(
                request.getUsuarioId(),
                request.getAceptaEncargos(),
                request.getNombreArtistico()
        );
        URI location = URI.create("/artistas/" + artista.getId());
        return ResponseEntity.created(location).body(ArtistaResponse.fromEntity(artista));
    }

    @PatchMapping("/{artistaId}")
    public ResponseEntity<ArtistaResponse> actualizarArtista(
            @PathVariable Long artistaId,
            @RequestBody ArtistaUpdateRequest request
    ) {
        PerfilArtista artista = artistaService.actualizarArtista(
                artistaId,
                request.getAceptaEncargos(),
                request.getNombreArtistico()
        );
        return ResponseEntity.ok(ArtistaResponse.fromEntity(artista));
    }

    @GetMapping("/{artistaId}/obras")
    public ResponseEntity<List<ObraArtistaResponse>> listarObrasDelArtista(
            @PathVariable Long artistaId
    ) {
        List<ObraArtistaResponse> obras = artistaService.listarObrasDelArtista(artistaId).stream()
                .map(ObraArtistaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(obras);
    }

}
