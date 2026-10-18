package com.uade.tpo.grupo11.gallery.controllers.marco;

import com.uade.tpo.grupo11.gallery.entities.Marco;

import java.math.BigDecimal;

// Lo que la API devuelve de los marcos. No exponemos la entidad: el public_id de Cloudinary es interno.
public record MarcoResponse(
        Long id,
        String nombre_marco,
        String color_marco,
        BigDecimal precio_marco,
        String url
) {
    // Traduce la entidad a lo que ve el cliente.
    public static MarcoResponse fromEntity(Marco marco) {
        return new MarcoResponse(
                marco.getId(),
                marco.getNombre_marco(),
                marco.getColor_marco(),
                marco.getPrecio_marco(),
                marco.getUrl_marco()
        );
    }
}
