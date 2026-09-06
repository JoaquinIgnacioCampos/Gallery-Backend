package com.uade.tpo.grupo11.gallery.controllers.imagen;

import lombok.Data;

@Data
public class ImagenRequest {

    private Long obra_id;
    private Integer orden_imagen;
    private byte[] contenido_imagen;
}
