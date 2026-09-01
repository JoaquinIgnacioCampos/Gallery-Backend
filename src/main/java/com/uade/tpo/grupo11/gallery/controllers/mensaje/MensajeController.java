package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.services.MensajeService;
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
    public ResponseEntity<Mensaje> getMensajePorId(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Mensaje> enviarMensaje(@RequestBody MensajeRequest request) {
        Mensaje mensaje = mensajeService.enviarMensaje(
                request.getEncargoId(),
                request.getUsuarioEmisorId(),
                request.getContenido());
        return ResponseEntity.ok(mensaje);
    }


}