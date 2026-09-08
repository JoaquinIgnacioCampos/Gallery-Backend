package com.uade.tpo.grupo11.gallery.services.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;

import java.util.List;

// Contrato: que sabe hacer el servicio de los mensajes. La implementacion es la que lleva la logica.
public interface MensajeService {
    // Busca el mensaje por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Mensaje getMensajeById(Long id);
    // Devuelve los mensajes del encargo.
    List<Mensaje> getMensajesByEncargo(Long encargoId);
    // Devuelve los mensajes del usuario.
    List<Mensaje> getMensajesByUsuario(Long usuarioId);
    // Crea el mensaje con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Mensaje createMensaje(Long encargoId, Long usuarioEmisorId, String contenido);
}
