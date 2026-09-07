package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenRequest;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.io.IOException;
import java.util.List;

// Contrato: que sabe hacer el servicio de imagenes. La logica vive en ImagenServiceImpl.
public interface ImagenService {
    List<Imagen> getImagenes();
    List<Imagen> getImagenesByObra(Long obraId);
    Imagen getImagenById(Long imagenId);
    Imagen createImagen(ImagenRequest request, Usuario usuarioActual) throws IOException;
    Imagen updateImagen(Long imagenId, ImagenRequest request, Usuario usuarioActual) throws IOException;
    void deleteImagen(Long imagenId, Usuario usuarioActual);
}
