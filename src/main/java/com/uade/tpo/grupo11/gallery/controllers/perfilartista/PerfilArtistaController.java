package com.uade.tpo.grupo11.gallery.controllers.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoResponse;
import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaResponse;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.encargo.EncargoService;
import com.uade.tpo.grupo11.gallery.services.perfilartista.PerfilArtistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recibe las peticiones HTTP de los perfiles de artista y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/artistas")
public class PerfilArtistaController {

    @Autowired
    private PerfilArtistaService perfilArtistaService;

    @Autowired
    private EncargoService encargoService;

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
            @Valid @RequestBody PerfilArtistaUpdateRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado
    ) {
        PerfilArtista perfilArtista = perfilArtistaService.updatePerfilArtista(perfilArtistaId, request, usuarioLogueado);
        return ResponseEntity.ok(PerfilArtistaResponse.fromEntity(perfilArtista));
    }

    // Actualiza el propio perfil: el id sale del token, no hace falta que el artista lo sepa.
    @PatchMapping("/me")
    public ResponseEntity<PerfilArtistaResponse> updatePerfilArtistaPropio(
            @Valid @RequestBody PerfilArtistaUpdateRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado
    ) {
        PerfilArtista perfilPropio = perfilArtistaService.getPerfilArtistaByUsuario(usuarioLogueado.getId());
        PerfilArtista perfilArtista = perfilArtistaService
                .updatePerfilArtista(perfilPropio.getId(), request, usuarioLogueado);
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

    // Las propias obras: el id sale del token, no hace falta que el artista lo sepa.
    @GetMapping("/me/obras")
    public ResponseEntity<List<PerfilArtistaObraResponse>> getObrasPropias(
            @AuthenticationPrincipal Usuario usuarioLogueado
    ) {
        PerfilArtista perfilPropio = perfilArtistaService.getPerfilArtistaByUsuario(usuarioLogueado.getId());
        List<PerfilArtistaObraResponse> obras = perfilArtistaService
                .getObrasByPerfilArtista(perfilPropio.getId()).stream()
                .map(PerfilArtistaObraResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(obras);
    }

    // Las ventas del artista: una fila por factura emitida a su nombre. El service
    // verifica que quien pregunta sea el dueño del perfil, no cualquier artista.
    @GetMapping("/{perfilArtistaId}/facturas")
    public ResponseEntity<List<FacturaResponse>> getFacturasByPerfilArtista(
            @PathVariable Long perfilArtistaId,
            @AuthenticationPrincipal Usuario usuarioLogueado
    ) {
        List<FacturaResponse> facturas = perfilArtistaService
                .getFacturasByPerfilArtista(perfilArtistaId, usuarioLogueado).stream()
                .map(FacturaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(facturas);
    }

    // Las propias ventas: el id sale del token, no hace falta que el artista lo sepa.
    @GetMapping("/me/facturas")
    public ResponseEntity<List<FacturaResponse>> getFacturasPropias(
            @AuthenticationPrincipal Usuario usuarioLogueado
    ) {
        PerfilArtista perfilPropio = perfilArtistaService.getPerfilArtistaByUsuario(usuarioLogueado.getId());
        List<FacturaResponse> facturas = perfilArtistaService
                .getFacturasByPerfilArtista(perfilPropio.getId(), usuarioLogueado).stream()
                .map(FacturaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(facturas);
    }

    // Los encargos que le pidieron al artista. El service verifica que quien pregunta
    // sea el dueño del perfil (o ADMIN), no cualquier artista.
    @GetMapping("/{perfilArtistaId}/encargos")
    public ResponseEntity<List<EncargoResponse>> getEncargosByPerfilArtista(
            @PathVariable Long perfilArtistaId,
            @AuthenticationPrincipal Usuario usuarioLogueado
    ) {
        List<EncargoResponse> encargos = encargoService
                .getEncargosByArtista(perfilArtistaId, usuarioLogueado).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(encargos);
    }

    // Los encargos propios, resolviendo el perfil desde la identidad del token: no hace
    // falta que el artista sepa (ni mande) el id de su propio perfil.
    @GetMapping("/me/encargos")
    public ResponseEntity<List<EncargoResponse>> getEncargosPropios(
            @AuthenticationPrincipal Usuario usuarioLogueado
    ) {
        PerfilArtista perfilPropio = perfilArtistaService.getPerfilArtistaByUsuario(usuarioLogueado.getId());
        List<EncargoResponse> encargos = encargoService
                .getEncargosByArtista(perfilPropio.getId(), usuarioLogueado).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(encargos);
    }
}
