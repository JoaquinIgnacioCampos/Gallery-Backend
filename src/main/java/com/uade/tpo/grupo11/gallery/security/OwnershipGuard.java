package com.uade.tpo.grupo11.gallery.security;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.exceptions.AccesoDenegadoException;

public class OwnershipGuard {

    private OwnershipGuard() {}

    // Acepta varios propietarios porque algunos recursos (ej: Encargo) son de mas de una
    // persona a la vez (el cliente que lo pidio y el artista que lo va a hacer).
    public static void verificar(Usuario usuarioActual, Long... propietariosId) {
        boolean esAdmin = usuarioActual.getRol_usuario() == Rol.ADMIN;
        boolean esDueño = false;
        for (Long propietarioId : propietariosId) {
            if (propietarioId != null && propietarioId.equals(usuarioActual.getId())) {
                esDueño = true;
                break;
            }
        }

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
