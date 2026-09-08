package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteRequest;
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
    // Crea la variante con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Variante createVariante(VarianteRequest request);
    // Actualiza la variante: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    Variante updateVariante(Long varianteId, VarianteRequest request);
    // Cambia solo el stock. Por eso es PATCH y no PUT.
    Variante actualizarStock(Long varianteId, Integer nuevoStock);
    // Elimina la variante de la base.
    void deleteVariante(Long varianteId);
}
