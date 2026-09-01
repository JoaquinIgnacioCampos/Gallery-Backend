package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;
import lombok.Data;


@Data
public class EncargoRequest {
    private Long artistaId;
    private Long usuarioId;
    private Long tamanioId;
    private Long marcoId;
    private TipoPintura tipoPintura;
    private TipoLienzo tipoLienzo;
    private String descripcionEncargo;
}
