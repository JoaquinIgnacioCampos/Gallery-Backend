package com.uade.tpo.grupo11.gallery.services.factura;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;

// Contrato: que sabe hacer el servicio de las facturas. La implementacion es la que lleva la logica.
public interface FacturaService {

    // Devuelve las facturas.
    Page<Factura> getFacturas(Usuario usuarioLogueado, PageRequest pageable);

    // Devuelve las facturas de la compra.
    Page<Factura> getFacturasByCompra(Long compraId, Usuario usuarioLogueado, PageRequest pageable);

    // Busca la factura por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Factura getFacturaById(Long facturaId, Usuario usuarioLogueado);

    // Crea la factura con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Factura createFactura(FacturaRequest request);

    // Actualiza la factura: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    Factura updateFactura(Long facturaId, FacturaRequest request);

    // Elimina la factura de la base.
    void deleteFactura(Long facturaId);
}
