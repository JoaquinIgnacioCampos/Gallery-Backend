package com.uade.tpo.grupo11.gallery.services.itemfactura;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.itemfactura.ItemFacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;

// Contrato: que sabe hacer el servicio de los items de la factura. La implementacion es la que lleva la logica.
public interface ItemFacturaService {
    // Busca el item de la factura por id. Si no existe, se lanza la excepcion y el handler responde 404.
    ItemFactura getItemFacturaById(Long id, Usuario usuarioActual);
    // Devuelve los items de la factura de la factura.
    Page<ItemFactura> getItemFacturasByFactura(Long facturaId, Usuario usuarioActual, PageRequest pageable);
    // Crea el item de la factura con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    ItemFactura createItemFactura(ItemFacturaRequest request);
}
