package com.uade.tpo.grupo11.gallery.services.usuario;

import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUserMailException;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUsernameException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Logica de negocio de los usuarios: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    // Crea el usuario con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Usuario createUsuario(UsuarioRequest usuarioRequest) {
        if (usuarioRequest.getNombre_usuario() == null || usuarioRequest.getNombre_usuario().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (usuarioRequest.getContrasenia_usuario() == null || usuarioRequest.getContrasenia_usuario().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        Optional<Usuario> result;
        result = usuarioRepository.findByEmail(usuarioRequest.getEmail_usuario());
        if (result.isPresent()) {
            throw new DuplicateUserMailException();
        }
        result = usuarioRepository.findByNombre(usuarioRequest.getNombre_usuario());
        if (result.isPresent()) {
            throw new DuplicateUsernameException();
        }

        Usuario usuario = new Usuario(usuarioRequest);
        usuario.setContrasenia_usuario(passwordEncoder.encode(usuarioRequest.getContrasenia_usuario()));

        return usuarioRepository.save(usuario);
    }

    // Busca el usuario por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Usuario getUsuario(Long usuario_id) {
        Optional<Usuario> result = usuarioRepository.findById(usuario_id);
        if (result.isPresent()) {
            return result.get();
        }

        throw new UsuarioNotFoundException(usuario_id);
    }

    // Actualiza el usuario: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Usuario updateUsuario(Long usuarioId, UsuarioRequest usuarioRequest, Usuario usuarioLogueado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        OwnershipGuard.verificar(usuarioLogueado, usuarioId);

        if (usuarioRequest.getEmail_usuario() != null) {
            usuarioRepository.findByEmail(usuarioRequest.getEmail_usuario())
                    .filter(existing -> !existing.getId().equals(usuarioId))
                    .ifPresent(existing -> { throw new DuplicateUserMailException(); });
        }

        if (usuarioRequest.getNombre_usuario() != null) {
            usuarioRepository.findByNombre(usuarioRequest.getNombre_usuario())
                    .filter(existing -> !existing.getId().equals(usuarioId))
                    .ifPresent(existing -> { throw new DuplicateUsernameException(); });
        }

        usuario.patchFrom(usuarioRequest);

        if (usuarioRequest.getContrasenia_usuario() != null) {
            usuario.setContrasenia_usuario(passwordEncoder.encode(usuarioRequest.getContrasenia_usuario()));
        }

        return usuarioRepository.save(usuario);
    }


    // Asignacion de permisos: un administrador decide que puede hacer cada cuenta.
    // El cambio vale enseguida, sin relogueo: los permisos se leen de la base en cada peticion.
    @Override
    public Usuario asignarRol(Long usuarioId, Rol nuevoRol) {

        if (nuevoRol == null) {
            throw new IllegalArgumentException("Hay que indicar el rol a asignar");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        usuario.setRol_usuario(nuevoRol);

        return usuarioRepository.save(usuario);
    }
}
