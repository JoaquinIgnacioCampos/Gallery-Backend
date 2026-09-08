package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


// Recibe las peticiones HTTP de los mensajes y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    // Inyecta las dependencias necesarias del servicio
    @Autowired
    private MensajeService mensajeService;

    // Busca el mensaje por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponse> getMensajeById(@PathVariable Long id) {
        return ResponseEntity.ok(MensajeResponse.fromEntity(mensajeService.getMensajeById(id)));
    }

    // Crea el mensaje con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @PostMapping
    public ResponseEntity<MensajeResponse> createMensaje(@Valid @RequestBody MensajeRequest request) {
        Mensaje mensaje = mensajeService.createMensaje(
                request.getEncargo_id(),
                request.getUsuario_emisor_id(),
                request.getContenido());
        return ResponseEntity.created(URI.create("/api/mensajes/" + mensaje.getId()))
                .body(MensajeResponse.fromEntity(mensaje));
    }


}
