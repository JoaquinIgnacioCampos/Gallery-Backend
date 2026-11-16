package com.uade.tpo.grupo11.gallery.controllers.encargo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

// Lo que el cliente manda al aceptar el precio: el monto que tiene a la vista. Si el artista lo cambio mientras tanto,
// el back no aprueba otro precio sin que el cliente lo vea.
@Data
public class AceptarPrecioRequest {

    @NotNull(message = "El precio que estas aceptando es obligatorio")
    private BigDecimal precio_acordado;
}
