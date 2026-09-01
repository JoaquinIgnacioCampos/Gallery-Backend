package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.controllers.Encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.exceptions.EncargoNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.EncargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EncargoServiceImpl implements EncargoService {

    @Autowired
    private EncargoRepository encargoRepository;
    @Autowired
    private ArtistaRepository artistaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;
    @Autowired
    private MarcoRepository marcoRepository;

    @Override
    public Encargo obtenerPorId(Long id) {
        return encargoRepository.findById(id)
                .orElseThrow(() -> new EncargoNotFoundException(id));
    }

    @Override
    public List<Encargo> obtenerPorArtista(Long artistaId) {
        return encargoRepository.findByArtistaId(artistaId);
    }

    @Override
    public List<Encargo> obtenerPorUsuario(Long usuarioId) {
        return encargoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Encargo crearEncargo(EncargoRequest request) {
        Artista artista = artistaRepository.findById(request.getArtistaId())
                .orElseThrow(() -> new ArtistaNotFoundException(request.getArtistaId()));

        if (!artista.getAceptaEncargos()) {
            throw new ArtistaNoAceptaEncargosException(artista.getId());
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuarioId()));
        TamanioLienzo tamanio = tamanioLienzoRepository.findById(request.getTamanioId())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getTamanioId()));
        Marco marco = marcoRepository.findById(request.getMarcoId())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarcoId()));

        Encargo encargo = new Encargo();
        encargo.setArtista(artista);
        encargo.setUsuario(usuario);
        encargo.setTamanioLienzo(tamanio);
        encargo.setMarco(marco);
        encargo.setTipoPintura(request.getTipoPintura());
        encargo.setTipoLienzo(request.getTipoLienzo());
        encargo.setEstadoEncargo(EstadoEncargo.PENDIENTE);
        encargo.setDescripcionEncargo(request.getDescripcionEncargo());

        return encargoRepository.save(encargo);
    }
}