package com.uade.tpo.grupo11.gallery.services.encargo;

import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNoAceptaEncargosException;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.EncargoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.MarcoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.TamanioLienzoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.EncargoRepository;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
import com.uade.tpo.grupo11.gallery.repositories.TamanioLienzoRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EncargoServiceImpl implements EncargoService {

    @Autowired
    private EncargoRepository encargoRepository;
    @Autowired
    private PerfilArtistaRepository artistaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;
    @Autowired
    private MarcoRepository marcoRepository;

    @Override
    public Encargo getEncargoById(Long id) {
        return encargoRepository.findById(id)
                .orElseThrow(() -> new EncargoNotFoundException(id));
    }

    @Override
    public List<Encargo> getEncargosByArtista(Long artistaId) {
        return encargoRepository.findByArtistaId(artistaId);
    }

    @Override
    public List<Encargo> getEncargosByUsuario(Long usuarioId) {
        return encargoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Encargo createEncargo(EncargoRequest request) {
        PerfilArtista artista = artistaRepository.findById(request.getArtista_id())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(request.getArtista_id()));

        if (!artista.isAcepta_encargos()) {
            throw new PerfilArtistaNoAceptaEncargosException(artista.getId());
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));
        TamanioLienzo tamanio = tamanioLienzoRepository.findById(request.getTamanio_id())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getTamanio_id()));
        Marco marco = marcoRepository.findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Encargo encargo = new Encargo();
        encargo.setArtista(artista);
        encargo.setUsuario(usuario);
        encargo.setTamanio(tamanio);
        encargo.setMarco(marco);
        encargo.setTipo_pintura(request.getTipo_pintura());
        encargo.setTipo_lienzo(request.getTipo_lienzo());
        encargo.setEstado_encargo(EstadoEncargo.PENDIENTE);
        encargo.setDescripcion_encargo(request.getDescripcion_encargo());

        return encargoRepository.save(encargo);
    }
}
