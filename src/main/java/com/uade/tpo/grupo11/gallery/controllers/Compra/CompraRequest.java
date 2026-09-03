package com.uade.tpo.grupo11.gallery.controllers.Compra;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CompraRequest {

    private Long usuarioId;
    private LocalDateTime fechaCompra;
    private BigDecimal totalCompra;
}