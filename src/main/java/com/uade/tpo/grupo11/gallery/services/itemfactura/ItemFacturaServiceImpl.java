package com.uade.tpo.grupo11.gallery.services.itemfactura;

import com.uade.tpo.grupo11.gallery.controllers.itemfactura.ItemFacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemFacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Logica de negocio de los items de la factura: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class ItemFacturaServiceImpl implements ItemFacturaService {

    @Autowired
    private ItemFacturaRepository itemFacturaRepository;
    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private CompraRepository compraRepository;
    @Autowired
    private MarcoRepository marcoRepository;
    @Autowired
    private VarianteRepository varianteRepository;

    // Busca el item de la factura por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public ItemFactura getItemFacturaById(Long id, Usuario usuarioActual) {
        ItemFactura item = itemFacturaRepository.findById(id)
                .orElseThrow(() -> new ItemFacturaNotFoundException(id));

        Factura factura = item.getFactura();
        OwnershipGuard.verificar(usuarioActual,
                factura.getArtista().getUsuario().getId(),
                factura.getCompra().getUsuario().getId());

        return item;
    }

    // Devuelve los items de la factura de la factura.
    @Override
    public List<ItemFactura> getItemFacturasByFactura(Long facturaId, Usuario usuarioActual) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new FacturaNotFoundException(facturaId));

        OwnershipGuard.verificar(usuarioActual,
                factura.getArtista().getUsuario().getId(),
                factura.getCompra().getUsuario().getId());

        List<ItemFactura> items = itemFacturaRepository.findByFacturaId(facturaId);
        if (items.isEmpty()) {
            throw new RecursoNoEncontradoException("La factura con id " + facturaId + " no tiene items cargados");
        }
        return items;
    }

    // Crea el item de la factura con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    @Transactional
    public ItemFactura createItemFactura(ItemFacturaRequest request) {

        Factura factura = facturaRepository.findById(request.getFactura_id())
                .orElseThrow(() -> new FacturaNotFoundException(request.getFactura_id()));

        Marco marco = marcoRepository.findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Variante variante = varianteRepository.findById(request.getVariante_id())
                .orElseThrow(() -> new VarianteNotFoundException(request.getVariante_id()));

        int cantidad = request.getCantidad_items();

        //No se puede vender más de lo que hay en stock
        if (variante.getStock_variante() < cantidad) {
            throw new StockInsuficienteException(
                    variante.getId(), cantidad, variante.getStock_variante());
        }

        //calcular el descuento si se necesita
        BigDecimal porcentajeDescuento = BigDecimal.ZERO;
        if (variante.getPorcentaje_descuento() != null
                && variante.getDescuento_hasta() != null
                && !LocalDate.now().isAfter(variante.getDescuento_hasta())) {
            porcentajeDescuento = BigDecimal.valueOf(variante.getPorcentaje_descuento())
                    .divide(BigDecimal.valueOf(100));
        }

        //Precio final variante + marco elegido
        BigDecimal precioUnitario = variante.getPrecio_variante().add(marco.getPrecio_marco());
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal montoDescuento = subtotal.multiply(porcentajeDescuento);
        BigDecimal total = subtotal.subtract(montoDescuento);

        //descontar el stock recién cuando la venta se confirma
        variante.setStock_variante(variante.getStock_variante() - cantidad);
        varianteRepository.save(variante);

        ItemFactura item = new ItemFactura();
        item.setFactura(factura);
        item.setMarco(marco);
        item.setVariante(variante);
        item.setCantidad_items(cantidad);
        item.setTotal_item(total);
        item.setDescuento(montoDescuento);
        item = itemFacturaRepository.save(item);

        factura.setPrecio_total_factura(factura.getPrecio_total_factura().add(total));
        facturaRepository.save(factura);

        Compra compra = factura.getCompra();
        compra.setTotal_compra(compra.getTotal_compra().add(total));
        compraRepository.save(compra);

        return item;
    }
}
