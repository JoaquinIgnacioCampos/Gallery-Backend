package com.uade.tpo.grupo11.gallery.controllers.factura;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;
import com.uade.tpo.grupo11.gallery.services.factura.DetalleDeVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Lo que se ve al abrir una venta en "Mis ventas": como y cuando se compro, quien compro (solo su nombre)
// y las obras vendidas con sus nombres y su portada, para no pedirlos aparte.
public record DetalleVentaResponse(
        Long factura_id,
        LocalDateTime fecha_compra,
        TipoEntrega tipo_entrega,
        String direccion_entrega,
        String nombre_comprador,
        List<Linea> items
) {
    // Una obra vendida. Los nombres de campo son los que ya usa la pantalla para mostrar una linea de compra.
    public record Linea(
            Long id,
            Long obra_id,
            String nombre_obra,
            String nombre_tamanio,
            String nombre_marco,
            int cantidad,
            BigDecimal descuento,
            BigDecimal subtotal,
            String portada_url
    ) {
    }

    public static DetalleVentaResponse fromDetalle(DetalleDeVenta detalle) {
        Compra compra = detalle.factura().getCompra();
        Usuario comprador = compra.getUsuario();

        return new DetalleVentaResponse(
                detalle.factura().getId(),
                compra.getFecha_compra(),
                compra.getTipo_entrega(),
                compra.getDireccion_entrega(),
                comprador.getNombre_persona() + " " + comprador.getApellido_persona(),
                detalle.lineas().stream().map(linea -> {
                    ItemFactura item = linea.item();
                    return new Linea(
                            item.getId(),
                            item.getVariante().getObra().getId(),
                            item.getVariante().getObra().getNombre_obra(),
                            item.getVariante().getTamanio().getNombre_tamanio(),
                            item.getMarco().getNombre_marco(),
                            item.getCantidad_items(),
                            item.getDescuento(),
                            item.getTotal_item(),
                            linea.portadaUrl()
                    );
                }).toList()
        );
    }
}
