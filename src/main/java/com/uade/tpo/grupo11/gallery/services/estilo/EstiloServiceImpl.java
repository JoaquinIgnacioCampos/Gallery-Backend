package com.uade.tpo.grupo11.gallery.services.estilo;

import com.uade.tpo.grupo11.gallery.controllers.estilo.EstiloRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.exceptions.EstiloDuplicadoException;
import com.uade.tpo.grupo11.gallery.exceptions.EstiloNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.EstiloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Logica de negocio de los estilos: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class EstiloServiceImpl implements EstiloService {

    @Autowired
    private EstiloRepository estiloRepository;

    // Busca el estilo por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Estilo obtenerPorId(Long id) {
        return estiloRepository.findById(id)
                .orElseThrow(() -> new EstiloNotFoundException(id));
    }

    // Devuelve los estilos.
    @Override
    public List<Estilo> obtenerTodos() {
        return estiloRepository.findAll();
    }

    // Crea el estilo con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Estilo crearEstilo(EstiloRequest request) {
        estiloRepository.findByNombreEstilo(request.getNombreEstilo())
                .ifPresent(e -> {
                    throw new EstiloDuplicadoException(request.getNombreEstilo());
                });

        Estilo estilo = new Estilo();
        estilo.setNombreEstilo(request.getNombreEstilo());

        return estiloRepository.save(estilo);
    }
}