package com.uade.tpo.grupo11.gallery.services.itemcarrito;

import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Variante;

// Si un item del carrito se puede comprar hoy y, si no, por que. Es la unica copia de la regla: el carrito la manda
// en cada item para que el front avise antes de pagar, y el checkout la usa para rechazar la compra.
//   disponible       : la obra sigue a la venta y alcanza el stock para la cantidad que hay en el carrito.
//   stockDisponible  : unidades que quedan de esa variante (0 si la obra ya no esta a la venta).
//   motivo           : el texto para la persona; null cuando esta disponible.
public record DisponibilidadDeItem(boolean disponible, int stockDisponible, String motivo) {

    public static DisponibilidadDeItem de(ItemCarrito item) {
        Variante variante = item.getVariante();
        Obra obra = variante.getObra();
        int stock = Math.max(variante.getStock_variante(), 0);

        if (obra.isEliminada() || !obra.isEn_venta()) {
            return new DisponibilidadDeItem(false, 0, "Esta obra ya no está a la venta.");
        }
        if (stock == 0) {
            return new DisponibilidadDeItem(false, 0, "Sin stock.");
        }
        if (stock < item.getCantidad()) {
            String quedan = stock == 1 ? "Solo queda 1 unidad." : "Solo quedan " + stock + " unidades.";
            return new DisponibilidadDeItem(false, stock, quedan);
        }
        return new DisponibilidadDeItem(true, stock, null);
    }
}
