package com.uade.tpo.grupo11.gallery.controllers.compra;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CompraRequest {

    private Long usuario_id;
    private LocalDateTime fecha_compra;
    private BigDecimal total_compra;
}
