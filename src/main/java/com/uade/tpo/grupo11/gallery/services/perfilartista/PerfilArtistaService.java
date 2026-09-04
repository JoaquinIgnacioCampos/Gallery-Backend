package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicatePerfilArtistaException;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;

public interface PerfilArtistaService {
    PerfilArtista getPerfilArtistaByUsuario(Long usuarioId) throws PerfilArtistaNotFoundException;
    PerfilArtista createPerfilArtista(Long usuarioId, PerfilArtistaRequest request) throws UsuarioNotFoundException, DuplicatePerfilArtistaException;
}
