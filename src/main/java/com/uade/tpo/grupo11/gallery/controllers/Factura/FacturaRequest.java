package com.uade.tpo.grupo11.gallery.controllers.Factura;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FacturaRequest {

    private Long artistaId;
    private Long compraId;
    private String detalleFactura;
    private BigDecimal precioTotalFactura;
    private LocalDateTime fechaCreacionFactura;
}
