package com.uade.tpo.grupo11.gallery.services.tamaniolienzo;

import com.uade.tpo.grupo11.gallery.controllers.tamaniolienzo.TamanioLienzoRequest;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;

import java.util.List;

// Contrato: que sabe hacer el servicio de los tamanios de lienzo. La implementacion es la que lleva la logica.
public interface TamanioLienzoService {
    // Busca el tamanio de lienzo por id. Si no existe, se lanza la excepcion y el handler responde 404.
    TamanioLienzo getTamanioLienzoById(Long id);
    // Devuelve los tamanios de lienzo.
    List<TamanioLienzo> getTamanioLienzos();
    // Crea el tamanio de lienzo con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    TamanioLienzo createTamanioLienzo(TamanioLienzoRequest request);
}
