package com.uade.tpo.grupo11.gallery.services.compra;

import com.uade.tpo.grupo11.gallery.controllers.compra.CompraRequest;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;

// Contrato: que sabe hacer el servicio de las compras. La implementacion es la que lleva la logica.
public interface CompraService {


    // Devuelve las compras.
    List<Compra> getCompras(Usuario usuarioActual);

    // Busca la compra por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Compra getCompraById(Long compraId, Usuario usuarioActual);

    // Crea la compra con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    Compra createCompra(CompraRequest request);

    // Te devuelve tus compras solo con el token
    List<Compra> getMisCompras(Usuario usuarioActual);

    // Actualiza la compra: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    Compra updateCompra(Long compraId, CompraRequest request);

    // Elimina la compra de la base.
    void deleteCompra(Long compraId);

    // Devuelve las compras del usuario.
    List<Compra> getComprasByUsuario(Long usuarioId, Usuario usuarioActual);

    // Crea una compra vacia para un usuario. El checkout usa su propio camino.
    Compra createCompraForUsuario(Long usuarioId);
}