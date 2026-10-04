package com.uade.tpo.grupo11.gallery.controllers.mensaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Lo que el cliente manda para crear o modificar los mensajes. Las relaciones viajan como ids.
@Data
public class MensajeRequest {

    @NotNull(message = "El encargo es obligatorio")
    private Long encargo_id;

    // El emisor NO se manda: es siempre el usuario logueado. Si viniera en el body,
    // cualquiera podria escribir un mensaje haciendose pasar por otro.

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    private String contenido;
}
