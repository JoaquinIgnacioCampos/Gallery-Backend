package com.uade.tpo.grupo11.gallery.services.factura;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.FacturaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ListaVaciaException;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.CompraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemFacturaRepository;

import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Logica de negocio de las facturas: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PerfilArtistaRepository artistaRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ItemFacturaRepository itemFacturaRepository;

    @Autowired
    private ImagenRepository imagenRepository;


    // Devuelve las facturas. Solo el ADMIN
    @Override
    public Page<Factura> getFacturas(Usuario usuarioLogueado, PageRequest pageable) {
        OwnershipGuard.soloAdmin(usuarioLogueado);
        Page<Factura> facturas = facturaRepository.findAll(pageable);
        if (facturas.isEmpty()) {
            throw new ListaVaciaException("No hay facturas registradas en el sistema");
        }
        return facturas;
    }


    // Devuelve las facturas de la compra. Solo el dueño de esa compra y los artistas que partcipan pueden acceder
    @Override
    public Page<Factura> getFacturasByCompra(Long compraId, Usuario usuarioLogueado, PageRequest pageable) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new CompraNotFoundException(compraId));

        OwnershipGuard.verificar(usuarioLogueado, compra.getUsuario().getId());

        Page<Factura> facturas = facturaRepository.findByCompraId(compraId, pageable);
        if (facturas.isEmpty()) {
            throw new ListaVaciaException("La compra con id " + compraId + " no tiene facturas");
        }
        return facturas;
    }


    // Busca la factura por id. Si no existe, se lanza la excepcion y el handler responde 404. Solo accede el dueño de esa compra y los artistas que partcipan
    @Override
    public Factura getFacturaById(Long facturaId, Usuario usuarioLogueado) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new FacturaNotFoundException(facturaId));

        OwnershipGuard.verificar(usuarioLogueado,
                factura.getArtista().getUsuario().getId(),
                factura.getCompra().getUsuario().getId());

        return factura;
    }


    // Devuelve el detalle de una venta. Los items llegan con su variante, obra, tamanio y marco en una sola
    // consulta, y las portadas de todas las obras en otra (con IN): no hay pedidos por obra.
    @Override
    @Transactional(readOnly = true)
    public DetalleDeVenta getDetalleDeVenta(Long facturaId, Usuario usuarioLogueado) {
        Factura factura = getFacturaById(facturaId, usuarioLogueado);

        List<ItemFactura> items = itemFacturaRepository.findDetalleByFacturaId(facturaId);

        List<Long> obraIds = new ArrayList<>();
        items.forEach(item -> obraIds.add(item.getVariante().getObra().getId()));

        Map<Long, String> portadaPorObra = new HashMap<>();
        if (!obraIds.isEmpty()) {
            imagenRepository.findPortadasDeObras(obraIds)
                    .forEach(fila -> portadaPorObra.putIfAbsent(fila.getObraId(), fila.getUrl()));
        }

        List<DetalleDeVenta.Linea> lineas = new ArrayList<>();
        items.forEach(item -> lineas.add(new DetalleDeVenta.Linea(
                item, portadaPorObra.get(item.getVariante().getObra().getId()))));

        return new DetalleDeVenta(factura, lineas);
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
