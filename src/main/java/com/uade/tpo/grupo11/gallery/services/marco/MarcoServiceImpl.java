package com.uade.tpo.grupo11.gallery.services.marco;

import com.uade.tpo.grupo11.gallery.controllers.marco.MarcoRequest;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.exceptions.MarcoNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

// Logica de negocio de los marcos: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class MarcoServiceImpl implements MarcoService {

    @Autowired
    private MarcoRepository marcoRepository;


    // Devuelve los marcos.
    @Override
    public List<Marco> getMarcos() {

        return marcoRepository.findAll();
    }


    // Busca el marco por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Marco getMarcoById(Long marcoId) {

        return marcoRepository
                .findById(marcoId)
                .orElseThrow(() -> new MarcoNotFoundException(marcoId));
    }


    // Crea el marco con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Marco createMarco(MarcoRequest request) throws IOException {

        if (request.getArchivo() == null || request.getArchivo().isEmpty()) {
            throw new IllegalArgumentException("El archivo de la imagen del marco es obligatorio");
        }

        Marco marco = Marco.builder()
                .nombre_marco(request.getNombre_marco())
                .color_marco(request.getColor_marco())
                .imagen_marco(request.getArchivo().getBytes())
                .precio_marco(request.getPrecio_marco())
                .build();

        return marcoRepository.save(marco);
    }


    // Actualiza el marco: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Marco updateMarco(
            Long marcoId,
            MarcoRequest request) throws IOException {

        Marco marco = marcoRepository
                .findById(marcoId)
                .orElseThrow(() -> new MarcoNotFoundException(marcoId));

        marco.setNombre_marco(request.getNombre_marco());
        marco.setColor_marco(request.getColor_marco());
        marco.setPrecio_marco(request.getPrecio_marco());

        if (request.getArchivo() != null && !request.getArchivo().isEmpty()) {
            marco.setImagen_marco(request.getArchivo().getBytes());
        }

        return marcoRepository.save(marco);
    }


    // Elimina el marco de la base.
    @Override
    public void deleteMarco(Long marcoId) {

        Marco marco = marcoRepository
                .findById(marcoId)
                .orElseThrow(() -> new MarcoNotFoundException(marcoId));

        marcoRepository.delete(marco);
    }
}
