package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaUpdateRequest;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Logica de negocio de los perfiles de artista: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class PerfilArtistaServiceImpl implements PerfilArtistaService {

    @Autowired
    private PerfilArtistaRepository perfilArtistaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ObraRepository obraRepository;
    @Autowired
    private FacturaRepository facturaRepository;

    // Devuelve los perfiles de artista del usuario.
    @Override
    public PerfilArtista getPerfilArtistaByUsuario(Long usuarioId) {
        return perfilArtistaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new PerfilArtistaNotFoundException(usuarioId));
    }

    // La identidad viene del contexto de seguridad, nunca de un id elegido por el cliente.
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public PerfilArtista createPerfilArtista(Usuario usuarioLogueado, PerfilArtistaRequest request) {
        Long usuarioId = usuarioLogueado.getId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        if (usuario.getRol_usuario() == Rol.ADMIN) {
            throw new AccesoDenegadoException("ya tiene permisos para hacer todo");
        }

        if (perfilArtistaRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new DuplicatePerfilArtistaException(usuarioId);
        }

        PerfilArtista perfilArtista = new PerfilArtista();
        perfilArtista.setUsuario(usuario);
        perfilArtista.setNombre_artistico(validateNombreArtistico(request.getNombre_artistico()));
        perfilArtista.setAcepta_encargos(request.isAcepta_encargos());
        perfilArtista = perfilArtistaRepository.save(perfilArtista);

        if (usuario.getRol_usuario() == Rol.CLIENTE) {
            usuario.setRol_usuario(Rol.ARTISTA_CLIENTE);
            usuarioRepository.save(usuario);
        }

        return perfilArtista;
    }

    // Devuelve los perfiles de artista.
    @Override
    public List<PerfilArtista> getPerfilArtistas() {
        List<PerfilArtista> perfiles = perfilArtistaRepository.findAll();

        if (perfiles.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron perfiles de artista registrados");
        }

        return perfiles;
    }

    // Busca el perfil de artista por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public PerfilArtista getPerfilArtistaById(Long perfilArtistaId) {
        return perfilArtistaRepository.findById(perfilArtistaId)
                .orElseThrow(() -> new PerfilArtistaNotFoundException(perfilArtistaId));
    }

    // Actualiza el perfil de artista: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public PerfilArtista updatePerfilArtista(Long perfilArtistaId, PerfilArtistaUpdateRequest request, Usuario usuarioLogueado) {
        PerfilArtista perfilArtista = getPerfilArtistaById(perfilArtistaId);

        OwnershipGuard.verificar(usuarioLogueado, perfilArtista.getUsuario().getId());

        if (request.getAcepta_encargos() != null) {
            perfilArtista.setAcepta_encargos(request.getAcepta_encargos());
        }
        if (request.getNombre_artistico() != null) {
            perfilArtista.setNombre_artistico(validateNombreArtistico(request.getNombre_artistico()));
        }

        return perfilArtistaRepository.save(perfilArtista);
    }

    // Devuelve las obras del perfil de artista.
    @Override
    public List<Obra> getObrasByPerfilArtista(Long perfilArtistaId) {
        getPerfilArtistaById(perfilArtistaId);
        List<Obra> obras = obraRepository.findByArtistaId(perfilArtistaId);

        if (obras.isEmpty()) {
            throw new RecursoNoEncontradoException("El artista con ID " + perfilArtistaId + " no tiene obras publicadas");
        }

        return obras;
    }

    // Devuelve las ventas del artista. Son datos de plata, asi que no alcanza con tener
    // el rol: hay que ser el dueño de ese perfil (o ADMIN), y eso lo decide el token.
    @Override
    public List<Factura> getFacturasByPerfilArtista(Long perfilArtistaId, Usuario usuarioLogueado) {
        PerfilArtista perfil = getPerfilArtistaById(perfilArtistaId);
        OwnershipGuard.verificar(usuarioLogueado, perfil.getUsuario().getId());

        List<Factura> facturas = facturaRepository.findByArtistaId(perfilArtistaId);

        if (facturas.isEmpty()) {
            throw new RecursoNoEncontradoException("El artista con ID " + perfilArtistaId + " no registra ventas ni facturas");
        }

        return facturas;
    }

    // Regla de negocio: el nombre artistico no puede venir vacio.
    private String validateNombreArtistico(String nombreArtistico) {
        if (nombreArtistico == null || nombreArtistico.isBlank()) {
            throw new PerfilArtistaInvalidDataException("El nombre artístico es obligatorio");
        }
        if (nombreArtistico.length() > 150) {
            throw new PerfilArtistaInvalidDataException(
                    "El nombre artístico no puede superar los 150 caracteres"
            );
        }
        return nombreArtistico.trim();
    }
}
