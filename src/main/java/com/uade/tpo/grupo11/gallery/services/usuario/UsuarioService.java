package com.uade.tpo.grupo11.gallery.services.usuario;

import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import org.springframework.stereotype.Service;

import java.util.List;

// Contrato: que sabe hacer el servicio de los usuarios. La implementacion es la que lleva la logica.
@Service
public interface UsuarioService {
    // Devuelve los usuarios.
    List<Usuario> getUsuarios();
    // Crea el usuario con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Usuario createUsuario(UsuarioRequest usuarioRequest);
    // Busca el usuario por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Usuario getUsuario(Long usuario_id);
    // Actualiza el usuario: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    Usuario updateUsuario(Long usuarioId, UsuarioRequest usuarioRequest, Usuario usuarioLogueado);

    // Administracion de cuentas (solo ADMIN): asignar permisos.
    Usuario asignarRol(Long usuarioId, Rol nuevoRol);
}
