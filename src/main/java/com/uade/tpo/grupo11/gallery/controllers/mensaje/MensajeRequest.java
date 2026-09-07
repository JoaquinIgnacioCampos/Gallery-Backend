package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MensajeRequest {

    @NotNull(message = "El encargo es obligatorio")
    private Long encargo_id;

    @NotNull(message = "El usuario emisor es obligatorio")
    private Long usuario_emisor_id;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    private String contenido;
}
