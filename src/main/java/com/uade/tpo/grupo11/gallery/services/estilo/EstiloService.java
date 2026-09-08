package com.uade.tpo.grupo11.gallery.services.estilo;

import com.uade.tpo.grupo11.gallery.controllers.estilo.EstiloRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;

import java.util.List;

// Contrato: que sabe hacer el servicio de los estilos. La implementacion es la que lleva la logica.
public interface EstiloService {
    // Busca el estilo por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Estilo obtenerPorId(Long id);
    // Devuelve los estilos.
    List<Estilo> obtenerTodos();
    // Crea el estilo con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Estilo crearEstilo(EstiloRequest request);
}