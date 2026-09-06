package com.uade.tpo.grupo11.gallery.services.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;

import java.util.List;

public interface MensajeService {
    Mensaje getMensajeById(Long id);
    List<Mensaje> getMensajesByEncargo(Long encargoId);
    List<Mensaje> getMensajesByUsuario(Long usuarioId);
    Mensaje createMensaje(Long encargoId, Long usuarioEmisorId, String contenido);
}
