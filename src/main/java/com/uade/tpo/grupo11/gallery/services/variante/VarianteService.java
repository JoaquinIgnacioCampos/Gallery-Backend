package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.util.List;

// Contrato: que sabe hacer el servicio de las variantes. La implementacion es la que lleva la logica.
public interface VarianteService {

    // Devuelve las variantes.
    List<Variante> getVariantes();
    // Devuelve las variantes de la obra.
    List<Variante> getVariantesByObra(Long obraId);
    // Busca la variante por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Variante getVarianteById(Long varianteId);
    Variante createVariante(VarianteRequest request, Usuario usuarioActual);
    Variante updateVariante(Long varianteId, VarianteRequest request, Usuario usuarioActual);
    Variante actualizarStock(Long varianteId, Integer nuevoStock, Usuario usuarioActual);
    void deleteVariante(Long varianteId, Usuario usuarioActual);
}
