package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.exceptions.MensajeNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MensajeServiceImpl implements MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;


    @Override
    public Mensaje obtenerPorId(UUID id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new MensajeNotFoundException(id));
    }

    @Override
    public List<Mensaje> obtenerPorEncargo(UUID encargoId) {
        return mensajeRepository.findByEncargoId(encargoId);
    }

}
