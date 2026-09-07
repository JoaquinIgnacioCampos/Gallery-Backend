package com.uade.tpo.grupo11.gallery.controllers.imagen;

import com.uade.tpo.grupo11.gallery.entities.Imagen;

public record ImagenResponse(
        Long id,
        Long obra_id,
        int orden_imagen,
        byte[] contenido_imagen
) {
    public static ImagenResponse fromEntity(Imagen imagen) {
        return new ImagenResponse(
                imagen.getId(),
                imagen.getObra().getId(),
                imagen.getOrden_imagen(),
                imagen.getContenido_imagen()
        );
    }
}
