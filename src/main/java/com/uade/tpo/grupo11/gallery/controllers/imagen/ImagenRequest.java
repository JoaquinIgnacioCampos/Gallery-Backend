package com.uade.tpo.grupo11.gallery.controllers.imagen;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// Lo que el cliente manda para crear o modificar las imagenes. Las relaciones viajan como ids.
@Data
public class ImagenRequest {

    @NotNull(message = "La obra es obligatoria")
    private Long obra_id;

    private Integer orden_imagen;

    private MultipartFile archivo;
}
