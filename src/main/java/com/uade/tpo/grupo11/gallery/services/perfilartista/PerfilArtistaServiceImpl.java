package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicatePerfilArtistaException;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilArtistaServiceImpl implements PerfilArtistaService {

    @Autowired
    private PerfilArtistaRepository perfilArtistaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public PerfilArtista getPerfilArtistaByUsuario(Long usuarioId) throws PerfilArtistaNotFoundException {
        return perfilArtistaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(PerfilArtistaNotFoundException::new);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public PerfilArtista createPerfilArtista(Long usuarioId, PerfilArtistaRequest request)
            throws UsuarioNotFoundException, DuplicatePerfilArtistaException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNotFoundException::new);

        if (perfilArtistaRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new DuplicatePerfilArtistaException();
        }

        PerfilArtista perfilArtista = new PerfilArtista();
        perfilArtista.setUsuario(usuario);
        perfilArtista.setNombre_artistico(request.getNombre_artistico());
        perfilArtista.setAcepta_encargos(request.isAcepta_encargos());
        perfilArtista = perfilArtistaRepository.save(perfilArtista);

        usuario.setRol_usuario(Rol.ARTISTA_CLIENTE);
        usuarioRepository.save(usuario);

        return perfilArtista;
    }
}
