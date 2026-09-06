package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteRequest;
import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.util.List;

public interface VarianteService {

    List<Variante> getVariantes();
    List<Variante> getVariantesByObra(Long obraId);
    Variante getVarianteById(Long varianteId);
    Variante createVariante(VarianteRequest request);
    Variante updateVariante(Long varianteId, VarianteRequest request);
    Variante actualizarStock(Long varianteId, Integer nuevoStock);
    void deleteVariante(Long varianteId);
}
