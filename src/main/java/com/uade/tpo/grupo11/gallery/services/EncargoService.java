package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.controllers.Encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;

import java.util.List;

public interface EncargoService {
    Encargo obtenerPorId(Long id);
    List<Encargo> obtenerPorArtista(Long artistaId);
    List<Encargo> obtenerPorUsuario(Long usuarioId);
    Encargo crearEncargo(EncargoRequest request);
}
