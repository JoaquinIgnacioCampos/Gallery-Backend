package com.uade.tpo.grupo11.gallery.services.compra;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;

import java.util.List;

public interface CompraService {
    List<Compra> getComprasByUsuario(Long usuarioId) throws UsuarioNotFoundException;
    List<Compra> crearCompra(Long usuarioId) throws UsuarioNotFoundException;
}
