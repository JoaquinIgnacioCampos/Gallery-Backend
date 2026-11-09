package com.uade.tpo.grupo11.gallery.controllers.adminmetricas;

import java.math.BigDecimal;

// Resumen general que ve el administrador en el panel de métricas.
public record AdminMetricasResponse(
        long ventas_totales,
        long ventas_hoy,
        BigDecimal ingresos_totales,
        BigDecimal ingresos_hoy,
        long encargos_totales,
        long encargos_activos,
        long usuarios_registrados,
        long obras_publicadas
) {
}