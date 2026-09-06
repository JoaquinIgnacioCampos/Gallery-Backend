package com.uade.tpo.grupo11.gallery.controllers.imagen;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImagenRequest {

    @NotNull(message = "La obra es obligatoria")
    private Long obra_id;

    // Opcional: si no viene, el Service la agrega al final de la galeria.
    private Integer orden_imagen;

    @NotNull(message = "El contenido de la imagen es obligatorio")
    private byte[] contenido_imagen;
}
