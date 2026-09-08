package com.uade.tpo.grupo11.gallery.controllers.estilo;

import com.uade.tpo.grupo11.gallery.entities.Estilo;

// Lo que la API devuelve de los estilos. No exponemos la entidad: evita recursion y datos de mas.
public record EstiloResponse(
        Long id,
        String nombreEstilo
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static EstiloResponse fromEntity(Estilo estilo) {
        return new EstiloResponse(
                estilo.getId(),
                estilo.getNombreEstilo()
        );
    }
}
