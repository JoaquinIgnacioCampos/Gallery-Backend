package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenRequest;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.io.IOException;
import java.util.List;

// Contrato: que sabe hacer el servicio de las imagenes. La implementacion es la que lleva la logica.
public interface ImagenService {
    // Devuelve las imagenes.
    List<Imagen> getImagenes();
    // Devuelve imagenes de la obra.
    List<Imagen> getImagenesByObra(Long obraId);
    // Busca la imagen por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Imagen getImagenById(Long imagenId);
    Imagen createImagen(ImagenRequest request, Usuario usuarioActual) throws IOException;
    Imagen updateImagen(Long imagenId, ImagenRequest request, Usuario usuarioActual) throws IOException;
    void deleteImagen(Long imagenId, Usuario usuarioActual);
}
