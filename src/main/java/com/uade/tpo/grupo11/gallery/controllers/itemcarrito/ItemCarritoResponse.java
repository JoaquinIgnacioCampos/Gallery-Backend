package com.uade.tpo.grupo11.gallery.controllers.itemcarrito;

import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.services.PrecioVigente;

import java.math.BigDecimal;

// Lo que la API devuelve de los items del carrito. No exponemos la entidad: evita recursion y datos de mas.
// Ademas de los ids viajan los nombres y los importes ya calculados: si no, para dibujar una
// sola linea del carrito el front tendria que pedir la variante, la obra y el marco por separado.
public record ItemCarritoResponse(
        Long id,
        Long marco_id,
        Long variante_id,
        Long carrito_id,
        Integer cantidad,
        String nombre_obra,
        String nombre_tamanio,
        String nombre_marco,
        BigDecimal precio_unitario,
        BigDecimal precio_marco,
        BigDecimal subtotal
) {
    // Traduce la entidad a lo que ve el cliente, con los importes ya resueltos.
    public static ItemCarritoResponse fromEntity(ItemCarrito itemCarrito) {

        Variante variante = itemCarrito.getVariante();
        Marco marco = itemCarrito.getMarco();

        // Mismo precio que va a cobrar el checkout: sale del helper compartido.
        BigDecimal precioUnitario = PrecioVigente.de(variante);
        BigDecimal precioMarco = marco != null ? marco.getPrecio_marco() : BigDecimal.ZERO;
        BigDecimal cantidad = BigDecimal.valueOf(itemCarrito.getCantidad());

        return new ItemCarritoResponse(
                itemCarrito.getId(),
                marco != null ? marco.getId() : null,
                variante.getId(),
                itemCarrito.getCarrito().getId(),
                itemCarrito.getCantidad(),
                variante.getObra().getNombre_obra(),
                variante.getTamanio().getNombre_tamanio(),
                marco != null ? marco.getNombre_marco() : null,
                precioUnitario,
                precioMarco,
                precioUnitario.add(precioMarco).multiply(cantidad)
        );
    }
}
