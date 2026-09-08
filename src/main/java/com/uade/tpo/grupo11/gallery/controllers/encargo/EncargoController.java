package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.controllers.mensaje.MensajeResponse;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.encargo.EncargoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los encargos y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/encargos")
public class EncargoController {

    @Autowired
    private EncargoService encargoService;

    @Autowired
    private MensajeService mensajeService;

    // Busca el encargo por id. Solo lo pueden ver el cliente y el artista del encargo.
    @GetMapping("/{id}")
    public ResponseEntity<EncargoResponse> getEncargoById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(encargoService.getEncargoById(id, usuarioLogueado)));
    }

    // Devuelve los encargos del artista. Solo el propio artista (o ADMIN).
    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<List<EncargoResponse>> getEncargosByArtista(
            @PathVariable Long artistaId,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<EncargoResponse> result = encargoService.getEncargosByArtista(artistaId, usuarioLogueado).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // Devuelve los encargos del usuario. Solo el propio usuario (o ADMIN).
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EncargoResponse>> getEncargosByUsuario(
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<EncargoResponse> result = encargoService.getEncargosByUsuario(usuarioId, usuarioLogueado).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // Crea el encargo con los datos del request. El cliente sale del usuario logueado.
    @PostMapping
    public ResponseEntity<EncargoResponse> createEncargo(
            @Valid @RequestBody EncargoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(encargoService.createEncargo(request, usuarioLogueado)));
    }
    // Devuelve los mensajes del encargo. Solo el cliente y el artista del encargo.
    @GetMapping("/{encargoId}/mensajes")
    public ResponseEntity<List<MensajeResponse>> getMensajesByEncargo(
            @PathVariable Long encargoId,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<MensajeResponse> result = mensajeService.getMensajesByEncargo(encargoId, usuarioLogueado).stream()
                .map(MensajeResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // Solo el artista DUENIO del encargo puede moverle el estado.
    // El rol lo filtra el SecurityConfig; la pertenencia la verifica el service.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EncargoResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {

        return ResponseEntity.ok(EncargoResponse.fromEntity(
                encargoService.cambiarEstado(id, request.getNuevoEstado(), usuarioLogueado)));
    }

}
