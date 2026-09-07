package com.uade.tpo.grupo11.gallery.services.marco;

import com.uade.tpo.grupo11.gallery.controllers.marco.MarcoRequest;
import com.uade.tpo.grupo11.gallery.entities.Marco;

import java.io.IOException;
import java.util.List;

// Contrato: que sabe hacer el servicio de los marcos. La implementacion es la que lleva la logica.
public interface MarcoService {

    // Devuelve los marcos.
    List<Marco> getMarcos();

    // Busca el marco por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Marco getMarcoById(Long marcoId);

    Marco createMarco(MarcoRequest request) throws IOException;

    Marco updateMarco(Long marcoId, MarcoRequest request) throws IOException;

    // Elimina el marco de la base.
    void deleteMarco(Long marcoId);
}
