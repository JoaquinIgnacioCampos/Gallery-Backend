package com.uade.tpo.grupo11.gallery.services.Marco;

import com.uade.tpo.grupo11.gallery.controllers.Marco.MarcoRequest;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarcoServiceImpl implements MarcoService {

    @Autowired
    private MarcoRepository marcoRepository;


    @Override
    public List<Marco> getMarcos() {

        return marcoRepository.findAll();
    }


    @Override
    public Marco getMarcoById(Long marcoId) {

        return marcoRepository
                .findById(marcoId)
                .orElseThrow(() ->
                        new RuntimeException("Marco no encontrado"));
    }


    @Override
    public Marco createMarco(MarcoRequest request) {

        Marco marco = Marco.builder()
                .nombreMarco(request.getNombreMarco())
                .colorMarco(request.getColorMarco())
                .imagenMarco(request.getImagenMarco())
                .precioMarco(request.getPrecioMarco())
                .build();

        return marcoRepository.save(marco);
    }


    @Override
    public Marco updateMarco(
            Long marcoId,
            MarcoRequest request) {

        Marco marco = marcoRepository
                .findById(marcoId)
                .orElseThrow(() ->
                        new RuntimeException("Marco no encontrado"));

        marco.setNombreMarco(request.getNombreMarco());
        marco.setColorMarco(request.getColorMarco());
        marco.setImagenMarco(request.getImagenMarco());
        marco.setPrecioMarco(request.getPrecioMarco());

        return marcoRepository.save(marco);
    }


    @Override
    public void deleteMarco(Long marcoId) {

        Marco marco = marcoRepository
                .findById(marcoId)
                .orElseThrow(() ->
                        new RuntimeException("Marco no encontrado"));

        marcoRepository.delete(marco);
    }
}
