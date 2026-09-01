package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.services.encargo.EncargoService;
import com.uade.tpo.grupo11.gallery.services.Mensaje.MensajeService;
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
    public ResponseEntity<Encargo> getEncargoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(encargoService.obtenerPorId(id));
    }

    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<List<Encargo>> getEncargosPorArtista(@PathVariable Long artistaId) {
        return ResponseEntity.ok(encargoService.obtenerPorArtista(artistaId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Encargo>> getEncargosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(encargoService.obtenerPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Encargo> crearEncargo(@RequestBody EncargoRequest request) {
        return ResponseEntity.ok(encargoService.crearEncargo(request));
    }
    @GetMapping("/{encargoId}/mensajes")
    public ResponseEntity<List<Mensaje>> getMensajesPorEncargo(@PathVariable Long encargoId) {
        return ResponseEntity.ok(mensajeService.obtenerPorEncargo(encargoId));
    }
}
