package com.uade.tpo.grupo11.gallery.services.Mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;

import java.util.List;

public interface MensajeService {
    Mensaje obtenerPorId(Long id);
    List<Mensaje> obtenerPorEncargo(Long encargoId);
    Mensaje enviarMensaje(Long encargoId, Long usuarioEmisorId, String contenido);

}
