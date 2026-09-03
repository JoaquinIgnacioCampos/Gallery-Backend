package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.entities.Imagen;

import java.util.List;

public interface ImagenService {
    List<Imagen> getListImagenes();
    Imagen getImagenById (Long id);
    Imagen createImagen (Imagen imagen);
    Imagen modificarImagen( Long ImagenId, Imagen imagen);
    void eliminarImagen (Long imagenId);

}
