package com.uade.tpo.grupo11.gallery.services.encargo;

import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;

import java.util.List;

public interface EncargoService {
    Encargo getEncargoById(Long id);
    List<Encargo> getEncargosByArtista(Long artistaId);
    List<Encargo> getEncargosByUsuario(Long usuarioId);
    Encargo createEncargo(EncargoRequest request);
    Encargo cambiarEstado(Long encargoId, EstadoEncargo nuevoEstado);
}
