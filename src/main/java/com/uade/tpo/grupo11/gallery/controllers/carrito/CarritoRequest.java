package com.uade.tpo.grupo11.gallery.controllers.carrito;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Lo que el cliente manda para crear o modificar los carritos. Las relaciones viajan como ids.
@Data
public class CarritoRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuario_id;

    private String direccion_cliente;
}
