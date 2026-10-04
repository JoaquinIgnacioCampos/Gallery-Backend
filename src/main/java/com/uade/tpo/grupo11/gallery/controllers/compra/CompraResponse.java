package com.uade.tpo.grupo11.gallery.controllers.compra;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Lo que la API devuelve de las compras. No exponemos la entidad: evita recursion y datos de mas.
public record CompraResponse(
        Long id,
        Long usuario_id,
        LocalDateTime fecha_compra,
        BigDecimal total_compra,
        String direccion_entrega,
        TipoEntrega tipo_entrega,
        BigDecimal costo_envio
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static CompraResponse fromEntity(Compra compra) {
        return new CompraResponse(
                compra.getId(),
                compra.getUsuario().getId(),
                compra.getFecha_compra(),
                compra.getTotal_compra(),
                compra.getDireccion_entrega(),
                compra.getTipo_entrega(),
                compra.getCosto_envio()
        );
    }
}
