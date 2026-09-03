package com.uade.tpo.grupo11.gallery.services.Marco;

import com.uade.tpo.grupo11.gallery.controllers.Marco.MarcoRequest;
import com.uade.tpo.grupo11.gallery.entities.Marco;

import java.util.List;

public interface MarcoService {

    List<Marco> getMarcos();

    Marco getMarcoById(Long marcoId);

    Marco createMarco(MarcoRequest request);

    Marco updateMarco(Long marcoId, MarcoRequest request);

    void deleteMarco(Long marcoId);
}
