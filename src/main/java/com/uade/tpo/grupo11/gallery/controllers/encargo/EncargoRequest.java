package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


// Lo que el cliente manda para crear o modificar los encargos. Las relaciones viajan como ids.
@Data
public class EncargoRequest {

    @NotNull(message = "El artista es obligatorio")
    private Long artista_id;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuario_id;

    @NotNull(message = "El tamanio de lienzo es obligatorio")
    private Long tamanio_id;

    @NotNull(message = "El marco es obligatorio")
    private Long marco_id;

    @NotNull(message = "El tipo de pintura es obligatorio")
    private TipoPintura tipo_pintura;

    @NotNull(message = "El tipo de lienzo es obligatorio")
    private TipoLienzo tipo_lienzo;

    private String descripcion_encargo;
}
