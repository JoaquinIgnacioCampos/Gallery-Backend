package com.uade.tpo.grupo11.gallery.controllers.estilo;

import com.uade.tpo.grupo11.gallery.entities.Estilo;

public record EstiloResponse(
        Long id,
        String nombreEstilo
) {
    public static EstiloResponse fromEntity(Estilo estilo) {
        return new EstiloResponse(
                estilo.getId(),
                estilo.getNombreEstilo()
        );
    }
}
