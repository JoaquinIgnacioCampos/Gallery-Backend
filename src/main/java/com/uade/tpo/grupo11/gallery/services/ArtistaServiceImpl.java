package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.ArtistaAlreadyExistsException;
import com.uade.tpo.grupo11.gallery.exceptions.ArtistaInvalidDataException;
import com.uade.tpo.grupo11.gallery.exceptions.ArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistaServiceImpl implements ArtistaService {


    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObraRepository obraRepository;

    @Override
    public List<PerfilArtista> listarArtistas() {
        return artistaRepository.findAll();
    }

    @Override
    public PerfilArtista getArtistaById(Long artistaId) {
        return artistaRepository.findById(artistaId)
                .orElseThrow(() -> new ArtistaNotFoundException(artistaId));
    }

    @Override
    public PerfilArtista crearArtista(
            Long usuarioId,
            Boolean aceptaEncargos,
            String nombreArtistico
    ) {
        if (usuarioId == null) {
            throw new ArtistaInvalidDataException("El usuarioId es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        if (artistaRepository.existsByUsuario(usuario)) {
            throw new ArtistaAlreadyExistsException(usuarioId);
        }

        PerfilArtista artista = new PerfilArtista();
        artista.setUsuario(usuario);
        artista.setAceptaEncargos(Boolean.TRUE.equals(aceptaEncargos));
        artista.setNombreArtistico(validarNombreArtistico(nombreArtistico));

        return artistaRepository.save(artista);
    }

    @Override
    public PerfilArtista actualizarArtista(
            Long artistaId,
            Boolean aceptaEncargos,
            String nombreArtistico
    ) {
        PerfilArtista artista = getArtistaById(artistaId);

        if (aceptaEncargos != null) {
            artista.setAceptaEncargos(aceptaEncargos);
        }
        if (nombreArtistico != null) {
            artista.setNombreArtistico(validarNombreArtistico(nombreArtistico));
        }

        return artistaRepository.save(artista);
    }

    @Override
    public List<Obra> listarObrasDelArtista(Long artistaId) {
        getArtistaById(artistaId);
        return obraRepository.findByArtistaId(artistaId);
    }

    private String validarNombreArtistico(String nombreArtistico) {
        if (nombreArtistico == null || nombreArtistico.isBlank()) {
            throw new ArtistaInvalidDataException("El nombre artístico es obligatorio");
        }
        if (nombreArtistico.length() > 150) {
            throw new ArtistaInvalidDataException(
                    "El nombre artístico no puede superar los 150 caracteres"
            );
        }
        return nombreArtistico.trim();
    }
}
