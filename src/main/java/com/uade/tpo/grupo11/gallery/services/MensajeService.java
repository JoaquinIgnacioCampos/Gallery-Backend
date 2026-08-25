package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;

import java.util.List;
import java.util.UUID;

public interface MensajeService {
    Mensaje obtenerPorId(UUID id);
    List<Mensaje> obtenerPorEncargo(UUID encargoId);
}
