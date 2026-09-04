package com.uade.tpo.grupo11.gallery.services.itemFactura;

import com.uade.tpo.grupo11.gallery.controllers.ItemFactura.ItemFacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.ItemFacturaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.StockInsuficienteException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
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
    public ItemFactura obtenerPorId(Long id) {
        return itemFacturaRepository.findById(id)
                .orElseThrow(() -> new ItemFacturaNotFoundException(id));
    }

    @Override
    public List<ItemFactura> obtenerPorFactura(Long facturaId) {
        return itemFacturaRepository.findByFacturaId(facturaId);
    }

    @Override
    @Transactional
    public ItemFactura crearItemFactura(ItemFacturaRequest request) {

        Factura factura = facturaRepository.findById(request.getFacturaId())
                .orElseThrow(() -> new FacturaNotFoundException(request.getFacturaId()));

        Marco marco = marcoRepository.findById(request.getMarcoId())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarcoId()));

        Variante variante = varianteRepository.findById(request.getVarianteId())
                .orElseThrow(() -> new VarianteNotFoundException(request.getVarianteId()));

        int cantidad = request.getCantidadItems();

        //No se puede vender más de lo que hay en stock
        if (variante.getStockVariante() < cantidad) {
            throw new StockInsuficienteException(
                    variante.getId(), cantidad, variante.getStockVariante());
        }

        //calcular el descuento si se necesita
        BigDecimal porcentajeDescuento = BigDecimal.ZERO;
        if (variante.getPorcentajeDescuento() != null
                && variante.getDescuentoHasta() != null
                && !LocalDate.now().isAfter(variante.getDescuentoHasta())) {
            porcentajeDescuento = BigDecimal.valueOf(variante.getPorcentajeDescuento())
                    .divide(BigDecimal.valueOf(100));
        }

        //Precio final variante + marco elegido
        BigDecimal precioUnitario = variante.getPrecioVariante().add(marco.getPrecioMarco());
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal montoDescuento = subtotal.multiply(porcentajeDescuento);
        BigDecimal total = subtotal.subtract(montoDescuento);

        //descontar el stock recién cuando la venta se confirma
        variante.setStockVariante(variante.getStockVariante() - cantidad);
        varianteRepository.save(variante);

        ItemFactura item = new ItemFactura();
        item.setFactura(factura);
        item.setMarco(marco);
        item.setVariante(variante);
        item.setCantidadItems(cantidad);
        item.setTotalItem(total);
        item.setDescuento(montoDescuento);

        return itemFacturaRepository.save(item);
    }
}