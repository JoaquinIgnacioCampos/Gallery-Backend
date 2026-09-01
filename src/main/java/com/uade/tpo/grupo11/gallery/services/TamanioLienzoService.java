package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.controllers.Tamanio_Lienzo.TamanioLienzoRequest;
import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;

import java.util.List;

public interface TamanioLienzoService {
    TamanioLienzo obtenerPorId(Long id);
    List<TamanioLienzo> obtenerTodos();
    TamanioLienzo crearTamanio(TamanioLienzoRequest request);
}


