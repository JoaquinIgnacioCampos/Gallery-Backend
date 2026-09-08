package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaUpdateRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;

// Contrato: que sabe hacer el servicio de los perfiles de artista. La implementacion es la que lleva la logica.
public interface PerfilArtistaService {

    // Devuelve los perfiles de artista del usuario.
    PerfilArtista getPerfilArtistaByUsuario(Long usuarioId);

    // Crea el perfil de artista con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    PerfilArtista createPerfilArtista(Long usuarioId, PerfilArtistaRequest request);

    // Devuelve los perfiles de artista.
    List<PerfilArtista> getPerfilArtistas();

    // Busca el perfil de artista por id. Si no existe, se lanza la excepcion y el handler responde 404.
    PerfilArtista getPerfilArtistaById(Long perfilArtistaId);

    // Actualiza el perfil de artista: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    PerfilArtista updatePerfilArtista(Long perfilArtistaId, PerfilArtistaUpdateRequest request, Usuario usuarioLogueado);

    // Devuelve las obras del perfil de artista.
    List<Obra> getObrasByPerfilArtista(Long perfilArtistaId);
}
