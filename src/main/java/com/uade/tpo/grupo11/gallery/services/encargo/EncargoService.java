package com.uade.tpo.grupo11.gallery.services.encargo;

import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;

import java.util.List;

// Contrato: que sabe hacer el servicio de los encargos. La implementacion es la que lleva la logica.
public interface EncargoService {
    // Busca el encargo por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Encargo getEncargoById(Long id);
    // Devuelve los encargos del artista.
    List<Encargo> getEncargosByArtista(Long artistaId);
    // Devuelve los encargos del usuario.
    List<Encargo> getEncargosByUsuario(Long usuarioId);
    // Crea el encargo con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Encargo createEncargo(EncargoRequest request);
    // Recibe al usuario logueado para verificar que el encargo sea suyo.
    Encargo cambiarEstado(Long encargoId, EstadoEncargo nuevoEstado, Usuario usuarioLogueado);
}
