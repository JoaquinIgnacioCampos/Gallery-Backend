package com.uade.tpo.grupo11.gallery.services.Compra;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CompraService {
    List<Compra> getListCompras();
    Compra getCompraById(Long compraId);
    Compra createCompra(Compra compra);
    Compra modificarCompra (Long compraId,Compra compra);
    void eliminarCompra (Long compraId);


