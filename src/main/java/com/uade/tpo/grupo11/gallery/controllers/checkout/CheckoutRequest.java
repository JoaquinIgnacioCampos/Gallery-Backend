package com.uade.tpo.grupo11.gallery.controllers.checkout;

import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull(message = "Debe indicar el tipo de entrega")
    private TipoEntrega tipo_entrega;
}