package com.uade.tpo.grupo11.gallery.controllers;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.services.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/{id}")
    public Mensaje getMensajePorId(@PathVariable UUID id) {
        return mensajeService.obtenerPorId(id);
    }

    @GetMapping("/encargo/{encargoId}")
    public List<Mensaje> getMensajesPorEncargo(@PathVariable UUID encargoId) {
        return mensajeService.obtenerPorEncargo(encargoId);
    }


}