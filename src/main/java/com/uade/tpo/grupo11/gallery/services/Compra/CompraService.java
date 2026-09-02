package com.uade.tpo.grupo11.gallery.services.Compra;

import com.uade.tpo.grupo11.gallery.controllers.Compra.CompraRequest;
import com.uade.tpo.grupo11.gallery.entities.Compra;

import java.util.List;

public interface CompraService {

    List<Compra> getCompras();

    Compra getCompraById(Long compraId);

    Compra createCompra(CompraRequest request);

    Compra updateCompra(Long compraId, CompraRequest request);

    void deleteCompra(Long compraId);
}