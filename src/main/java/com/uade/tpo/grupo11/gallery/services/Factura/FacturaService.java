package com.uade.tpo.grupo11.gallery.services.factura;

import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaRequest;
import com.uade.tpo.grupo11.gallery.entities.Factura;

import java.util.List;

public interface FacturaService {

    List<Factura> getFacturas();

    Factura getFacturaById(Long facturaId);

    Factura createFactura(FacturaRequest request);

    Factura updateFactura(Long facturaId, FacturaRequest request);

    void deleteFactura(Long facturaId);
}
