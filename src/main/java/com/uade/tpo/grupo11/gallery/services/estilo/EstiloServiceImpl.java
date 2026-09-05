package com.uade.tpo.grupo11.gallery.services.estilo;

import com.uade.tpo.grupo11.gallery.controllers.estilo.EstiloRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.exceptions.EstiloDuplicadoException;
import com.uade.tpo.grupo11.gallery.exceptions.EstiloNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.EstiloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstiloServiceImpl implements EstiloService {

    @Autowired
    private EstiloRepository estiloRepository;

    @Override
    public Estilo obtenerPorId(Long id) {
        return estiloRepository.findById(id)
                .orElseThrow(() -> new EstiloNotFoundException(id));
    }

    @Override
    public List<Estilo> obtenerTodos() {
        return estiloRepository.findAll();
    }

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