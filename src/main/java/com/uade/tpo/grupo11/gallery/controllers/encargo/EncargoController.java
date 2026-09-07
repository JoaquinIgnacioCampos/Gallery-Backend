package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.controllers.mensaje.MensajeResponse;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.encargo.EncargoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encargos")
public class EncargoController {

    @Autowired
    private EncargoService encargoService;

    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/{id}")
    public ResponseEntity<EncargoResponse> getEncargoById(@PathVariable Long id) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(encargoService.getEncargoById(id)));
    }

    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<List<EncargoResponse>> getEncargosByArtista(@PathVariable Long artistaId) {
        List<EncargoResponse> result = encargoService.getEncargosByArtista(artistaId).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EncargoResponse>> getEncargosByUsuario(@PathVariable Long usuarioId) {
        List<EncargoResponse> result = encargoService.getEncargosByUsuario(usuarioId).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<EncargoResponse> createEncargo(@Valid @RequestBody EncargoRequest request) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(encargoService.createEncargo(request)));
    }
    @GetMapping("/{encargoId}/mensajes")
    public ResponseEntity<List<MensajeResponse>> getMensajesByEncargo(@PathVariable Long encargoId) {
        List<MensajeResponse> result = mensajeService.getMensajesByEncargo(encargoId).stream()
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
