package com.uade.tpo.grupo11.gallery.services.checkout;

import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoVacioException;
import com.uade.tpo.grupo11.gallery.exceptions.DireccionRequeridaException;
import com.uade.tpo.grupo11.gallery.exceptions.StockInsuficienteException;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemCarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemFacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import com.uade.tpo.grupo11.gallery.services.PrecioVigente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Logica de negocio de checkout: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ItemFacturaRepository itemFacturaRepository;

    @Autowired
    private VarianteRepository varianteRepository;

    private static final String DIRECCION_RETIRO_GALERIA = "Galería Grupo 11, Brandsen 805, CABA";
    private static final BigDecimal COSTO_ENVIO_DOMICILIO = new BigDecimal("1500.00");

    /**
     * Convierte el carrito de un usuario en una compra.
     *
     * @Transactional: toca varias tablas (Compra, Factura, ItemFactura, Variante, ItemCarrito).
     * Si algo falla en el medio, se deshace TODO. Es el principio de atomicidad de ACID:
     * no puede quedar una compra a medias ni stock descontado sin factura.
     */
    @Override
    @Transactional
    public Compra checkout(Long usuarioId, TipoEntrega tipoEntregaElegido) {

        // 1) Buscamos el carrito del usuario y sus items.
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new CarritoNotFoundException(usuarioId));

        List<ItemCarrito> items = itemCarritoRepository.findByCarritoId(carrito.getId());

        if (items.isEmpty()) {
            throw new CarritoVacioException(carrito.getId());
        }

        // 1.5) Si eligió envío a domicilio, necesita tener una dirección cargada.
        // Si eligió retiro, no importa si tiene dirección o no.
        String direccionEntrega;
        BigDecimal costoEnvio;

        if (tipoEntregaElegido == TipoEntrega.ENVIO_DOMICILIO) {
            if (carrito.getDireccion_cliente() == null || carrito.getDireccion_cliente().isBlank()) {
                throw new DireccionRequeridaException(
                        "Para elegir envío a domicilio primero tenés que cargar tu dirección");
            }
            direccionEntrega = carrito.getDireccion_cliente();
            costoEnvio = COSTO_ENVIO_DOMICILIO;
        } else {
            direccionEntrega = DIRECCION_RETIRO_GALERIA;
            costoEnvio = BigDecimal.ZERO;
        }

        // 2) Validamos el stock de TODO antes de tocar nada.
        for (ItemCarrito item : items) {
            Variante variante = item.getVariante();
            if (variante.getStock_variante() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        variante.getId(), item.getCantidad(), variante.getStock_variante());
            }
        }

        // 3) Agrupamos los items por artista, siguiendo variante -> obra -> artista.
        Map<PerfilArtista, List<ItemCarrito>> itemsPorArtista = new LinkedHashMap<>();
        for (ItemCarrito item : items) {
            PerfilArtista artista = item.getVariante().getObra().getArtista();
            itemsPorArtista.computeIfAbsent(artista, a -> new java.util.ArrayList<>()).add(item);
        }

        // 4) La Compra: la "bolsa" del cliente. Se crea una sola.
        Compra compra = Compra.builder()
                .usuario(carrito.getUsuario())
                .fecha_compra(LocalDateTime.now())
                .total_compra(BigDecimal.ZERO)
                .direccion_entrega(direccionEntrega)
                .tipo_entrega(tipoEntregaElegido)
                .costo_envio(costoEnvio)
                .build();
        compra = compraRepository.save(compra);

        BigDecimal totalGeneral = BigDecimal.ZERO;

        // 5) Una factura por artista, colgando de la compra.
        for (Map.Entry<PerfilArtista, List<ItemCarrito>> entrada : itemsPorArtista.entrySet()) {

            Factura factura = Factura.builder()
                    .artista(entrada.getKey())
                    .compra(compra)
                    .precio_total_factura(BigDecimal.ZERO)
                    .fecha_creacion_factura(LocalDateTime.now())
                    .build();
            factura = facturaRepository.save(factura);

            BigDecimal totalFactura = BigDecimal.ZERO;

            for (ItemCarrito item : entrada.getValue()) {

                Variante variante = item.getVariante();
                BigDecimal cantidad = BigDecimal.valueOf(item.getCantidad());

                // Precio del día: el de la variante menos el descuento si está vigente.
                BigDecimal precioLista = variante.getPrecio_variante();
                BigDecimal precioConDescuento = aplicarDescuento(variante);
                BigDecimal descuentoUnitario = precioLista.subtract(precioConDescuento);

                // El marco se eligió al agregar al carrito y suma al precio.
                BigDecimal precioMarco = item.getMarco() != null
                        ? item.getMarco().getPrecio_marco()
                        : BigDecimal.ZERO;

                BigDecimal totalItem = precioConDescuento.add(precioMarco).multiply(cantidad);

                // El item de factura guarda los importes CONGELADOS.
                ItemFactura itemFactura = new ItemFactura();
                itemFactura.setFactura(factura);
                itemFactura.setVariante(variante);
                itemFactura.setMarco(item.getMarco());
                itemFactura.setCantidad_items(item.getCantidad());
                itemFactura.setTotal_item(totalItem);
                itemFactura.setDescuento(descuentoUnitario.multiply(cantidad));
                itemFacturaRepository.save(itemFactura);

                // Descontamos el stock vendido con un UPDATE atómico.
                int filasAfectadas = varianteRepository.descontarStock(variante.getId(), item.getCantidad());
                if (filasAfectadas == 0) {
                    throw new StockInsuficienteException(
                            variante.getId(), item.getCantidad(), variante.getStock_variante());
                }

                totalFactura = totalFactura.add(totalItem);
            }

            factura.setPrecio_total_factura(totalFactura);
            facturaRepository.save(factura);

            totalGeneral = totalGeneral.add(totalFactura);
        }

        // 6) El total de la compra incluye el subtotal de los ítems más el costo de envío (si aplica).
        compra.setTotal_compra(totalGeneral.add(costoEnvio));
        compra = compraRepository.save(compra);

        // 7) Se vacía el carrito: sus ítems ya viajaron a las facturas.
        itemCarritoRepository.deleteAll(items);

        return compra;
    }


    // El precio efectivo se calcula, no se guarda: si la fecha del descuento vencio,
    // vuelve solo al precio de lista sin que nadie toque nada.
    private BigDecimal aplicarDescuento(Variante variante) {

        // La cuenta se mudo a PrecioVigente para que el carrito muestre exactamente
        // el mismo precio que despues se cobra aca.
        return PrecioVigente.de(variante);
    }
}
