package com.uade.tpo.grupo11.gallery.services.compra;

import com.uade.tpo.grupo11.gallery.controllers.compra.CompraRequest;
import com.uade.tpo.grupo11.gallery.entities.Compra;

import java.util.List;

public interface CompraService {

    List<Compra> getCompras();

    Compra getCompraById(Long compraId);

    Compra createCompra(CompraRequest request);

    Compra updateCompra(Long compraId, CompraRequest request);

    void deleteCompra(Long compraId);

    List<Compra> getComprasByUsuario(Long usuarioId);

    Compra createCompraForUsuario(Long usuarioId);
}