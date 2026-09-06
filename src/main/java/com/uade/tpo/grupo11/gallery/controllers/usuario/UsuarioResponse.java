package com.uade.tpo.grupo11.gallery.controllers.usuario;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;

import java.util.Date;

public record UsuarioResponse(
        Long id,
        String nombre_usuario,
        String nombre_persona,
        String apellido_persona,
        String email_usuario,
        String telefono_usuario,
        Date fecha_creacion,
        Rol rol_usuario
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre_usuario(),
                usuario.getNombre_persona(),
                usuario.getApellido_persona(),
                usuario.getEmail_usuario(),
                usuario.getTelefono_usuario(),
                usuario.getFecha_creacion(),
                usuario.getRol_usuario()
        );
    }
}
