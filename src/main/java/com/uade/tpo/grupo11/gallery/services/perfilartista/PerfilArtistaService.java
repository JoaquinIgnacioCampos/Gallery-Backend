package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaUpdateRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;

import java.util.List;

public interface PerfilArtistaService {

    PerfilArtista getPerfilArtistaByUsuario(Long usuarioId);

    PerfilArtista createPerfilArtista(Long usuarioId, PerfilArtistaRequest request);

    List<PerfilArtista> getPerfilArtistas();

    PerfilArtista getPerfilArtistaById(Long perfilArtistaId);

    PerfilArtista updatePerfilArtista(Long perfilArtistaId, PerfilArtistaUpdateRequest request);

    List<Obra> getObrasByPerfilArtista(Long perfilArtistaId);
}
