package com.uade.tpo.grupo11.gallery.controllers.carrito;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarritoRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuario_id;

    private String direccion_cliente;
}
