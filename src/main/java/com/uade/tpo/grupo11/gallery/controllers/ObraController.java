package com.uade.tpo.grupo11.gallery.controllers;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.services.ObraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// Atiende peticiones web y devuelve JSON (no vistas HTML)
@RestController
// Todos los endpoints de esta clase cuelgan de /obras
@RequestMapping("/obras")
public class ObraController {

    // Spring inyecta la implementación de ObraService; el controller solo conoce la interfaz
    @Autowired
    private ObraService servicioObra;

    // GET /obras/{obraId} — el nombre del parámetro debe coincidir con el de la ruta
    @GetMapping("/{obraId}")
    public ResponseEntity<Obra> getObraById(@PathVariable UUID obraId) {
        return ResponseEntity.ok(servicioObra.getObraById(obraId)); // 200 OK
    }
}