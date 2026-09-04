package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.controllers.Tamanio_Lienzo.TamanioLienzoRequest;
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
    public TamanioLienzo obtenerPorId(Long id) {
        return tamanioLienzoRepository.findById(id)
                .orElseThrow(() -> new TamanioLienzoNotFoundException(id));
    }

    @Override
    public List<TamanioLienzo> obtenerTodos() {
        return tamanioLienzoRepository.findAll();
    }

    @Override
    public TamanioLienzo crearTamanio(TamanioLienzoRequest request) {
        TamanioLienzo tamanio = new TamanioLienzo();
        tamanio.setNombreTamanio(request.getNombreTamanio());
        tamanio.setAnchoLienzo(request.getAnchoLienzo());
        tamanio.setLargoLienzo(request.getLargoLienzo());

        return tamanioLienzoRepository.save(tamanio);
    }
}
