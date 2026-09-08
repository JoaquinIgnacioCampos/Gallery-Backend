package com.uade.tpo.grupo11.gallery.security;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.exceptions.AccesoDenegadoException;

public class OwnershipGuard {

    private OwnershipGuard() {}

    public static void verificar(Usuario usuarioActual, Long propietarioId) {
        boolean esAdmin = usuarioActual.getRol_usuario() == Rol.ADMIN;
        boolean esDueño = propietarioId != null && propietarioId.equals(usuarioActual.getId());

        if (!esAdmin && !esDueño) {
            throw new AccesoDenegadoException("No tenes permiso para operar sobre este recurso");
        }
    }

    public static void soloAdmin(Usuario usuarioActual) {
        if (usuarioActual.getRol_usuario() != Rol.ADMIN) {
            throw new AccesoDenegadoException("Esta accion requiere rol ADMIN");
        }
    }
}
