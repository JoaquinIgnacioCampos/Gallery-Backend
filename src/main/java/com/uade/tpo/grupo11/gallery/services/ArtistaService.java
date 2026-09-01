package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.List;

public interface ArtistaService {
    List<PerfilArtista> listarArtistas();
    PerfilArtista getArtistaById(Long artistaId);
    PerfilArtista crearArtista(Long usuarioId, Boolean aceptaEncargos, String nombreArtistico);
    PerfilArtista actualizarArtista(Long artistaId, Boolean aceptaEncargos, String nombreArtistico);
    List<Obra> listarObrasDelArtista(Long artistaId);
}
