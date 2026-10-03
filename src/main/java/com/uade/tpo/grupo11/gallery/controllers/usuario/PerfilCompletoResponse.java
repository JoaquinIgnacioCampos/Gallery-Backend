package com.uade.tpo.grupo11.gallery.controllers.usuario;

import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaResponse;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

// Perfil completo del usuario logueado: sus datos de cuenta, mas los de artista
// si en algun momento se paso a ARTISTA_CLIENTE. Si todavia no es artista,
// perfil_artista viaja en null
public record PerfilCompletoResponse(
        UsuarioResponse usuario,
        PerfilArtistaResponse perfil_artista
) {
    public static PerfilCompletoResponse of(Usuario usuario, PerfilArtista perfilArtista) {
        return new PerfilCompletoResponse(
                UsuarioResponse.fromEntity(usuario),
                perfilArtista != null ? PerfilArtistaResponse.fromEntity(perfilArtista) : null
        );
    }
}