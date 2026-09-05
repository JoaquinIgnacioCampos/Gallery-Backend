package com.uade.tpo.grupo11.gallery.services.usuario;

import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsuarioService {
    List<Usuario> getUsuarios();
    Usuario createUsuario(UsuarioRequest usuarioRequest);
    Usuario getUsuario(Long usuario_id);
    Usuario updateUsuario(Long usuarioId, UsuarioRequest usuarioRequest);
}
