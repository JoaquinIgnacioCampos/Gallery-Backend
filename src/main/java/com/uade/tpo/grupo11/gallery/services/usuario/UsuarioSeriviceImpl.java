package com.uade.tpo.grupo11.gallery.services.usuario;

import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUserMailException;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUsernameException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioSeriviceImpl implements UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario createUsuario(UsuarioRequest usuarioRequest) throws DuplicateUserMailException, DuplicateUsernameException {
        Optional<Usuario> result;
        result = usuarioRepository.findByEmail(usuarioRequest.getEmail_usuario());
        if (result.isPresent()) {
            throw new DuplicateUserMailException();
        }
        result = usuarioRepository.findByNombre(usuarioRequest.getNombre_usuario());
        if (result.isPresent()) {
            throw new DuplicateUsernameException();
        }

        return usuarioRepository.save(new Usuario(usuarioRequest));
    }

    @Override
    public Usuario getUsuario(Long usuario_id) throws UsuarioNotFoundException {
        Optional<Usuario> result = usuarioRepository.findById(usuario_id);
        if (result.isPresent()) {
            return result.get();
        }

        throw new UsuarioNotFoundException();
    }

    @Override
    public Usuario updateUsuario(Long usuarioId, UsuarioRequest usuarioRequest) throws DuplicateUserMailException, DuplicateUsernameException, UsuarioNotFoundException {
        Optional<Usuario> result, duplicate_mail, duplicate_username;
        duplicate_mail = usuarioRepository.findByEmail(usuarioRequest.getEmail_usuario());
        if (duplicate_mail.isPresent()) {
            throw new DuplicateUserMailException();
        }

        duplicate_username = usuarioRepository.findByNombre(usuarioRequest.getNombre_usuario());
        if (duplicate_username.isPresent()) {
            throw new DuplicateUsernameException();
        }

        result = usuarioRepository.findById(usuarioId);
        if (!result.isPresent()) {
            throw new UsuarioNotFoundException();
        }

        result.get().setEmail_usuario(usuarioRequest.getEmail_usuario());
        return usuarioRepository.save(result.get());
    }
}
