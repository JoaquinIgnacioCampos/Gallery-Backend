package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


// Recibe las peticiones HTTP de los mensajes y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    // Inyecta las dependencias necesarias del servicio
    @Autowired
    private MensajeService mensajeService;

    // Busca el mensaje por id. Solo lo pueden ver el cliente y el artista del encargo.
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponse> getMensajeById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(MensajeResponse.fromEntity(mensajeService.getMensajeById(id, usuarioLogueado)));
    }

    // Crea el mensaje. El emisor sale del usuario logueado, nunca del body.
    @PostMapping
    public ResponseEntity<MensajeResponse> createMensaje(
            @Valid @RequestBody MensajeRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        Mensaje mensaje = mensajeService.createMensaje(
                request.getEncargo_id(),
                usuarioLogueado,
                request.getContenido());
        return ResponseEntity.created(URI.create("/api/mensajes/" + mensaje.getId()))
                .body(MensajeResponse.fromEntity(mensaje));
    }


}
