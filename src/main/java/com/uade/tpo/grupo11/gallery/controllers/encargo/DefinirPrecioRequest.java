package com.uade.tpo.grupo11.gallery.controllers.encargo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

// Lo que el artista manda para cargar o cambiar el precio de un encargo.
@Data
public class DefinirPrecioRequest {

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio tiene que ser mayor a cero")
    private BigDecimal precio_acordado;
}
