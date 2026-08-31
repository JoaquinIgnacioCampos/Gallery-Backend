package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.services.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/{id}")
    public Mensaje getMensajePorId(@PathVariable Long id) {
        return mensajeService.obtenerPorId(id);
    }

    @GetMapping("/encargo/{encargoId}")
    public List<Mensaje> getMensajesPorEncargo(@PathVariable Long encargoId) {
        return mensajeService.obtenerPorEncargo(encargoId);
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