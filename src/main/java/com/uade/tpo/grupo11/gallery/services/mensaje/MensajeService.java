package com.uade.tpo.grupo11.gallery.services.mensaje;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import java.util.List;

// Contrato: que sabe hacer el servicio de los mensajes. La implementacion es la que lleva la logica.
public interface MensajeService {
    // Busca el mensaje por id. Solo lo pueden ver el cliente y el artista del encargo.
    Mensaje getMensajeById(Long id, Usuario usuarioLogueado);
    // Devuelve los mensajes del encargo. Solo el cliente y el artista del encargo.
    Page<Mensaje> getMensajesByEncargo(Long encargoId, Usuario usuarioLogueado, PageRequest pageable);
    // Devuelve los mensajes enviados por el usuario. Solo el propio usuario (o ADMIN).
    Page<Mensaje> getMensajesByUsuario(Long usuarioId, Usuario usuarioLogueado, PageRequest pageable);
    // Crea el mensaje. El emisor es siempre el usuario logueado, nunca un id del body.
    Mensaje createMensaje(Long encargoId, Usuario usuarioLogueado, String contenido);
}
