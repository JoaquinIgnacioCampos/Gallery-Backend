package com.uade.tpo.grupo11.gallery.services.tamaniolienzo;

import com.uade.tpo.grupo11.gallery.controllers.tamaniolienzo.TamanioLienzoRequest;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;

import java.util.List;

public interface TamanioLienzoService {
    TamanioLienzo getTamanioLienzoById(Long id);
    List<TamanioLienzo> getTamanioLienzos();
    TamanioLienzo createTamanioLienzo(TamanioLienzoRequest request);
}
