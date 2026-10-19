package com.uade.tpo.grupo11.gallery.repositories;

// Una fila de ItemFacturaRepository.contarObrasPorFacturas: la factura y cuantas obras (unidades) lleva.
// Spring arma estos objetos solo, a partir de los alias (facturaId, cantidad) de la consulta.
public interface CantidadDeObras {
    Long getFacturaId();
    Long getCantidad();
}
