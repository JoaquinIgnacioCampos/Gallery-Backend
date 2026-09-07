package com.uade.tpo.grupo11.gallery.services.marco;

import com.uade.tpo.grupo11.gallery.controllers.marco.MarcoRequest;
import com.uade.tpo.grupo11.gallery.entities.Marco;

import java.io.IOException;
import java.util.List;

public interface MarcoService {

    List<Marco> getMarcos();

    Marco getMarcoById(Long marcoId);

    Marco createMarco(MarcoRequest request) throws IOException;

    Marco updateMarco(Long marcoId, MarcoRequest request) throws IOException;

    void deleteMarco(Long marcoId);
}
