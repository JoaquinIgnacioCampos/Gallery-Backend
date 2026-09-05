package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;
import lombok.Data;


@Data
public class EncargoRequest {
    private Long artista_id;
    private Long usuario_id;
    private Long tamanio_id;
    private Long marco_id;
    private TipoPintura tipo_pintura;
    private TipoLienzo tipo_lienzo;
    private String descripcion_encargo;
}
