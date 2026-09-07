package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;

import java.time.LocalDateTime;

public record EncargoResponse(
        Long id,
        Long artista_id,
        Long usuario_id,
        Long tamanio_id,
        Long marco_id,
        TipoPintura tipo_pintura,
        TipoLienzo tipo_lienzo,
        EstadoEncargo estado_encargo,
        String descripcion_encargo,
        LocalDateTime fecha_creacion_encargo
) {
    public static EncargoResponse fromEntity(Encargo encargo) {
        return new EncargoResponse(
                encargo.getId(),
                encargo.getArtista().getId(),
                encargo.getUsuario().getId(),
                encargo.getTamanio().getId(),
                encargo.getMarco().getId(),
                encargo.getTipo_pintura(),
                encargo.getTipo_lienzo(),
                encargo.getEstado_encargo(),
                encargo.getDescripcion_encargo(),
                encargo.getFecha_creacion_encargo()
        );
    }
}
