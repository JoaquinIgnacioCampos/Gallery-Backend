package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenRequest;
import com.uade.tpo.grupo11.gallery.entities.Imagen;

import java.util.List;

public interface ImagenService {
    List<Imagen> getImagenes();
    List<Imagen> getImagenesByObra(Long obraId);
    Imagen getImagenById(Long imagenId);
    Imagen createImagen(ImagenRequest request);
    Imagen updateImagen(Long imagenId, ImagenRequest request);
    void deleteImagen(Long imagenId);
}
