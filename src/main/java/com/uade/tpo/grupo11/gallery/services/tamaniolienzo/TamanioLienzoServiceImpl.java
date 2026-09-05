package com.uade.tpo.grupo11.gallery.services.tamaniolienzo;

import com.uade.tpo.grupo11.gallery.controllers.tamaniolienzo.TamanioLienzoRequest;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.exceptions.TamanioLienzoNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.TamanioLienzoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TamanioLienzoServiceImpl implements TamanioLienzoService {

    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;

    @Override
    public TamanioLienzo getTamanioLienzoById(Long id) {
        return tamanioLienzoRepository.findById(id)
                .orElseThrow(() -> new TamanioLienzoNotFoundException(id));
    }

    @Override
    public List<TamanioLienzo> getTamanioLienzos() {
        return tamanioLienzoRepository.findAll();
    }

    @Override
    public TamanioLienzo createTamanioLienzo(TamanioLienzoRequest request) {
        TamanioLienzo tamanio = new TamanioLienzo();
        tamanio.setNombre_tamanio(request.getNombre_tamanio());
        tamanio.setAncho_lienzo(request.getAncho_lienzo());
        tamanio.setLargo_lienzo(request.getLargo_lienzo());

        return tamanioLienzoRepository.save(tamanio);
    }
}
