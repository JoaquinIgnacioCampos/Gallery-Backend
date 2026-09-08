package com.uade.tpo.grupo11.gallery.controllers.imagen;

import com.uade.tpo.grupo11.gallery.entities.Imagen;

// Lo que la API devuelve de las imagenes. No exponemos la entidad: evita recursion y datos de mas.
public record ImagenResponse(
        Long id,
        Long obra_id,
        int orden_imagen,
        byte[] contenido_imagen
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static ImagenResponse fromEntity(Imagen imagen) {
        return new ImagenResponse(
                imagen.getId(),
                imagen.getObra().getId(),
                imagen.getOrden_imagen(),
                imagen.getContenido_imagen()
        );
    }
}
