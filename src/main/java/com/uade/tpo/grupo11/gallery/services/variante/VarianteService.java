package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.util.List;

// Contrato: que sabe hacer el servicio de variantes. La logica vive en VarianteServiceImpl.
// El controller depende de esta interfaz y nunca de la implementacion: por eso se puede
// reemplazar el como sin tocar a quien lo usa.
public interface VarianteService {

    List<Variante> getVariantes();
    List<Variante> getVariantesByObra(Long obraId);
    Variante getVarianteById(Long varianteId);
    Variante createVariante(VarianteRequest request, Usuario usuarioActual);
    Variante updateVariante(Long varianteId, VarianteRequest request, Usuario usuarioActual);
    Variante actualizarStock(Long varianteId, Integer nuevoStock, Usuario usuarioActual);
    void deleteVariante(Long varianteId, Usuario usuarioActual);
}
