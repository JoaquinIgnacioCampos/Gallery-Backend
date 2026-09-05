package com.uade.tpo.grupo11.gallery.services.estilo;

import com.uade.tpo.grupo11.gallery.controllers.estilo.EstiloRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;

import java.util.List;

public interface EstiloService {
    Estilo obtenerPorId(Long id);
    List<Estilo> obtenerTodos();
    Estilo crearEstilo(EstiloRequest request);
}