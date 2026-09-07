package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    // Inyecta las dependencias necesarias del servicio
    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponse> getMensajeById(@PathVariable Long id) {
        return ResponseEntity.ok(MensajeResponse.fromEntity(mensajeService.getMensajeById(id)));
    }

    @PostMapping
    public ResponseEntity<MensajeResponse> createMensaje(@Valid @RequestBody MensajeRequest request) {
        Mensaje mensaje = mensajeService.createMensaje(
                request.getEncargo_id(),
                request.getUsuario_emisor_id(),
                request.getContenido());
        return ResponseEntity.ok(MensajeResponse.fromEntity(mensaje));
    }


}
