package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.services.encargo.EncargoService;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
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
    public ResponseEntity<Encargo> getEncargoById(@PathVariable Long id) {
        return ResponseEntity.ok(encargoService.getEncargoById(id));
    }

    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<List<Encargo>> getEncargosByArtista(@PathVariable Long artistaId) {
        return ResponseEntity.ok(encargoService.getEncargosByArtista(artistaId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Encargo>> getEncargosByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(encargoService.getEncargosByUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Encargo> createEncargo(@RequestBody EncargoRequest request) {
        return ResponseEntity.ok(encargoService.createEncargo(request));
    }
    @GetMapping("/{encargoId}/mensajes")
    public ResponseEntity<List<Mensaje>> getMensajesByEncargo(@PathVariable Long encargoId) {
        return ResponseEntity.ok(mensajeService.getMensajesByEncargo(encargoId));
    }
}
