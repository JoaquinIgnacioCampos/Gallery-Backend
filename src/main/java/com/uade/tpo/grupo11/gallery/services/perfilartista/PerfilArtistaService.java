package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaUpdateRequest;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;
import java.util.Optional;

// Contrato: que sabe hacer el servicio de los perfiles de artista. La implementacion es la que lleva la logica.
public interface PerfilArtistaService {

    // Devuelve los perfiles de artista del usuario.
    PerfilArtista getPerfilArtistaByUsuario(Long usuarioId);

    // Crea el perfil exclusivamente para el usuario autenticado.
    PerfilArtista createPerfilArtista(Usuario usuarioLogueado, PerfilArtistaRequest request);

    // Devuelve los perfiles de artista.
    List<PerfilArtista> getPerfilArtistas();

    // Busca el perfil de artista por id. Si no existe, se lanza la excepcion y el handler responde 404.
    PerfilArtista getPerfilArtistaById(Long perfilArtistaId);

    // Actualiza el perfil de artista: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    PerfilArtista updatePerfilArtista(Long perfilArtistaId, PerfilArtistaUpdateRequest request, Usuario usuarioLogueado);

    // Devuelve las obras del perfil de artista.
    List<Obra> getObrasByPerfilArtista(Long perfilArtistaId);

    // Devuelve las ventas del artista: las facturas emitidas a su nombre.
    List<Factura> getFacturasByPerfilArtista(Long perfilArtistaId, Usuario usuarioLogueado);

    Optional<PerfilArtista> getPerfilArtistaByUsuarioOptional(Long usuarioId);
}
