package com.uade.tpo.grupo11.gallery.services.encargo;

import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;

import java.util.List;

public interface EncargoService {
    Encargo getEncargoById(Long id);
    List<Encargo> getEncargosByArtista(Long artistaId);
    List<Encargo> getEncargosByUsuario(Long usuarioId);
    Encargo createEncargo(EncargoRequest request);
}
