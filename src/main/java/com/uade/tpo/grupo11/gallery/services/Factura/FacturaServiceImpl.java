package com.uade.tpo.grupo11.gallery.services.Factura;

import com.uade.tpo.grupo11.gallery.controllers.Factura.FacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.Artista;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.repositories.ArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private CompraRepository compraRepository;


    @Override
    public List<Factura> getFacturas() {

        return facturaRepository.findAll();
    }


    @Override
    public Factura getFacturaById(Long facturaId) {

        return facturaRepository
                .findById(facturaId)
                .orElseThrow(() ->
                        new RuntimeException("Factura no encontrada"));
    }


    @Override
    public Factura createFactura(FacturaRequest request) {

        Artista artista = artistaRepository
                .findById(request.getArtistaId())
                .orElseThrow(() ->
                        new RuntimeException("Artista no encontrado"));

        Compra compra = compraRepository
                .findById(request.getCompraId())
                .orElseThrow(() ->
                        new RuntimeException("Compra no encontrada"));

        Factura factura = Factura.builder()
                .artista(artista)
                .compra(compra)
                .detalleFactura(request.getDetalleFactura())
                .precioTotalFactura(request.getPrecioTotalFactura())
                .fechaCreacionFactura(request.getFechaCreacionFactura())
                .build();

        return facturaRepository.save(factura);
    }


    @Override
    public Factura updateFactura(
            Long facturaId,
            FacturaRequest request) {

        Factura factura = facturaRepository
                .findById(facturaId)
                .orElseThrow(() ->
                        new RuntimeException("Factura no encontrada"));

        Artista artista = artistaRepository
                .findById(request.getArtistaId())
                .orElseThrow(() ->
                        new RuntimeException("Artista no encontrado"));

        Compra compra = compraRepository
                .findById(request.getCompraId())
                .orElseThrow(() ->
                        new RuntimeException("Compra no encontrada"));

        factura.setArtista(artista);
        factura.setCompra(compra);
        factura.setDetalleFactura(request.getDetalleFactura());
        factura.setPrecioTotalFactura(request.getPrecioTotalFactura());
        factura.setFechaCreacionFactura(request.getFechaCreacionFactura());

        return facturaRepository.save(factura);
    }


    @Override
    public void deleteFactura(Long facturaId) {

        Factura factura = facturaRepository
                .findById(facturaId)
                .orElseThrow(() ->
                        new RuntimeException("Factura no encontrada"));

        facturaRepository.delete(factura);
    }
}
