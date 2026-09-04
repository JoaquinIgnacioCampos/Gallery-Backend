package com.uade.tpo.grupo11.gallery.services.usuario;

import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUserMailException;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUsernameException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsuarioService {
    List<Usuario> getUsuarios();
    Usuario createUsuario(UsuarioRequest usuarioRequest) throws DuplicateUserMailException, DuplicateUsernameException;
    Usuario getUsuario(Long usuario_id) throws UsuarioNotFoundException;
    Usuario updateUsuario(Long usuarioId, UsuarioRequest usuarioRequest) throws DuplicateUserMailException, DuplicateUsernameException, UsuarioNotFoundException;
}
