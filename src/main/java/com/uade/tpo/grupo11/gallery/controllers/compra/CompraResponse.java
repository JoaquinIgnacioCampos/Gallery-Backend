package com.uade.tpo.grupo11.gallery.controllers.compra;

import com.uade.tpo.grupo11.gallery.entities.Compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraResponse(
        Long id,
        Long usuario_id,
        LocalDateTime fecha_compra,
        BigDecimal total_compra
) {
    public static CompraResponse fromEntity(Compra compra) {
        return new CompraResponse(
                compra.getId(),
                compra.getUsuario().getId(),
                compra.getFecha_compra(),
                compra.getTotal_compra()
        );
    }
}
