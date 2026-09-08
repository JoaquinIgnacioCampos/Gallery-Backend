package com.uade.tpo.grupo11.gallery.services.factura;

import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.exceptions.FacturaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.CompraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// Logica de negocio de las facturas: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PerfilArtistaRepository artistaRepository;

    @Autowired
    private CompraRepository compraRepository;


    // Devuelve las facturas.
    @Override
    public List<Factura> getFacturas() {

        return facturaRepository.findAll();
    }


    // Devuelve las facturas de la compra.
    @Override
    public List<Factura> getFacturasByCompra(Long compraId) {

        if (!compraRepository.existsById(compraId)) {
            throw new CompraNotFoundException(compraId);
        }

        return facturaRepository.findByCompraId(compraId);
    }


    // Busca la factura por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Factura getFacturaById(Long facturaId) {

        return facturaRepository
                .findById(facturaId)
                .orElseThrow(() -> new FacturaNotFoundException(facturaId));
    }


    // Crea la factura con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Factura createFactura(FacturaRequest request) {

        PerfilArtista artista = artistaRepository
                .findById(request.getArtista_id())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(request.getArtista_id()));

        Compra compra = compraRepository
                .findById(request.getCompra_id())
                .orElseThrow(() -> new CompraNotFoundException(request.getCompra_id()));

        Factura factura = Factura.builder()
                .artista(artista)
                .compra(compra)
                .detalle_factura(request.getDetalle_factura())
                .precio_total_factura(BigDecimal.ZERO)
                .fecha_creacion_factura(request.getFecha_creacion_factura())
                .build();

        return facturaRepository.save(factura);
    }


    // Actualiza la factura: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Factura updateFactura(
            Long facturaId,
            FacturaRequest request) {

        Factura factura = facturaRepository
                .findById(facturaId)
                .orElseThrow(() -> new FacturaNotFoundException(facturaId));

        PerfilArtista artista = artistaRepository
                .findById(request.getArtista_id())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(request.getArtista_id()));

        Compra compra = compraRepository
                .findById(request.getCompra_id())
                .orElseThrow(() -> new CompraNotFoundException(request.getCompra_id()));

        factura.setArtista(artista);
        factura.setCompra(compra);
        factura.setDetalle_factura(request.getDetalle_factura());
        factura.setFecha_creacion_factura(request.getFecha_creacion_factura());

        return facturaRepository.save(factura);
    }


    // Elimina la factura de la base.
    @Override
    public void deleteFactura(Long facturaId) {

        Factura factura = facturaRepository
                .findById(facturaId)
                .orElseThrow(() -> new FacturaNotFoundException(facturaId));

        facturaRepository.delete(factura);
    }
}
