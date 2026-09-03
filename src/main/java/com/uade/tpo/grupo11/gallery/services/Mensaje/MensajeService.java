package com.uade.tpo.grupo11.gallery.services.Mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;

import java.util.List;

public interface MensajeService {
    Mensaje obtenerPorId(Long id);
    List<Mensaje> obtenerPorEncargo(Long encargoId);
    List<Mensaje> obtenerPorUsuario(Long usuarioId) throws UsuarioNotFoundException;
    Mensaje enviarMensaje(Long encargoId, Long usuarioEmisorId, String contenido);

}
