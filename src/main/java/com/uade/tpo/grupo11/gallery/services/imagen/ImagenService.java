package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.entities.Imagen;

import java.util.List;

public interface ImagenService {
    List<Imagen> getImagenes();
    Imagen getImagenById(Long id);
    Imagen createImagen(Imagen imagen);
    Imagen updateImagen(Long imagenId, Imagen imagen);
    void deleteImagen(Long imagenId);

}
