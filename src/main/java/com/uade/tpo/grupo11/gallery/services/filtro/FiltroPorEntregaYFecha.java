package com.uade.tpo.grupo11.gallery.services.filtro;

import java.time.LocalDate;
import java.time.LocalDateTime;

// El filtro de las listas de compras y de ventas: un rango de fechas y como se entrego. Los dos son opcionales.
// Se arma una sola vez a partir de lo que llega en la URL y valida los datos; las consultas reciben sus campos.
//   desde / hasta    : limites ya completos (sin nulos: sin limite es una fecha muy lejana), para no depender de
//                      parametros nulos de fecha en la consulta. "hasta" es EXCLUSIVO: es el comienzo del dia siguiente.
//   filtroDeEntrega  : TODAS_LAS_ENTREGAS, ENVIO_A_DOMICILIO, RETIRO_EN_EL_LOCAL o SOLO_ENCARGOS (una compra de un
//                      encargo no tiene tipo de entrega: no se envia).
public record FiltroPorEntregaYFecha(LocalDateTime desde, LocalDateTime hasta, int filtroDeEntrega) {

    public static final int TODAS_LAS_ENTREGAS = 0;
    public static final int ENVIO_A_DOMICILIO = 1;
    public static final int RETIRO_EN_EL_LOCAL = 2;
    public static final int SOLO_ENCARGOS = 3;

    private static final LocalDateTime SIN_LIMITE_INFERIOR = LocalDateTime.of(1970, 1, 1, 0, 0);
    private static final LocalDateTime SIN_LIMITE_SUPERIOR = LocalDateTime.of(2999, 1, 1, 0, 0);

    // desde y hasta incluyen el dia que se indica. entrega: todas (por defecto), envio, retiro o encargo.
    public static FiltroPorEntregaYFecha armar(LocalDate desde, LocalDate hasta, String entrega) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta.");
        }

        int filtroDeEntrega;
        if (entrega == null || entrega.isBlank() || entrega.equals("todas")) {
            filtroDeEntrega = TODAS_LAS_ENTREGAS;
        } else if (entrega.equals("envio")) {
            filtroDeEntrega = ENVIO_A_DOMICILIO;
        } else if (entrega.equals("retiro")) {
            filtroDeEntrega = RETIRO_EN_EL_LOCAL;
        } else if (entrega.equals("encargo")) {
            filtroDeEntrega = SOLO_ENCARGOS;
        } else {
            throw new IllegalArgumentException("Entrega invalida: usar todas, envio, retiro o encargo.");
        }

        return new FiltroPorEntregaYFecha(
                desde == null ? SIN_LIMITE_INFERIOR : desde.atStartOfDay(),
                hasta == null ? SIN_LIMITE_SUPERIOR : hasta.plusDays(1).atStartOfDay(),
                filtroDeEntrega);
    }
}
