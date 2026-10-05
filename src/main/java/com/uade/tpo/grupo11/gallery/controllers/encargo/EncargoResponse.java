package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Lo que la API devuelve de los encargos. No exponemos la entidad: evita recursion y datos de mas.
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
        BigDecimal precio_acordado,
        LocalDateTime fecha_creacion_encargo
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
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
                encargo.getPrecio_acordado(),
                encargo.getFecha_creacion_encargo()
        );
    }
}
