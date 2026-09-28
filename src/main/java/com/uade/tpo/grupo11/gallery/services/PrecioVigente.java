package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

// El precio que realmente se cobra hoy por una variante. Vive aca y no dentro del
// checkout porque el carrito necesita mostrar el mismo numero que despues se factura:
// si cada uno lo calculara por su lado, se podrian desincronizar.
public final class PrecioVigente {

    private PrecioVigente() {
    }

    // Aplica el descuento solo si existe y no vencio. Si no, devuelve el precio de lista.
    public static BigDecimal de(Variante variante) {

        Integer porcentaje = variante.getPorcentaje_descuento();
        LocalDate hasta = variante.getDescuento_hasta();

        boolean vigente = porcentaje != null
                && porcentaje > 0
                && (hasta == null || !hasta.isBefore(LocalDate.now()));

        if (!vigente) {
            return variante.getPrecio_variante();
        }

        BigDecimal factor = BigDecimal.valueOf(100 - porcentaje)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return variante.getPrecio_variante().multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
