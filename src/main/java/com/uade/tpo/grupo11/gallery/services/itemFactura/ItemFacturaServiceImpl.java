package com.uade.tpo.grupo11.gallery.services.itemfactura;

import com.uade.tpo.grupo11.gallery.controllers.itemfactura.ItemFacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.FacturaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ItemFacturaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.MarcoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.StockInsuficienteException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemFacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ItemFacturaServiceImpl implements ItemFacturaService {

    @Autowired
    private ItemFacturaRepository itemFacturaRepository;
    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private MarcoRepository marcoRepository;
    @Autowired
    private VarianteRepository varianteRepository;

    @Override
    public ItemFactura getItemFacturaById(Long id) {
        return itemFacturaRepository.findById(id)
                .orElseThrow(() -> new ItemFacturaNotFoundException(id));
    }

    @Override
    public List<ItemFactura> getItemFacturasByFactura(Long facturaId) {
        return itemFacturaRepository.findByFacturaId(facturaId);
    }

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

        return itemFacturaRepository.save(item);
    }
}