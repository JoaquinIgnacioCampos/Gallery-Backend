package com.uade.tpo.grupo11.gallery.services.itemcarrito;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ItemCarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.MarcoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemCarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Logica de negocio de los items del carrito: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class ItemCarritoServiceImpl implements ItemCarritoService {

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private MarcoRepository marcoRepository;

    @Autowired
    private VarianteRepository varianteRepository;


    // Devuelve los items del carrito.
    @Override
    public List<ItemCarrito> getItemsCarrito(Usuario usuarioActual) {

        // Ver todos los items de todos los carritos es una vista administrativa.
        OwnershipGuard.soloAdmin(usuarioActual);

        return itemCarritoRepository.findAll();
    }


    // Busca el item del carrito por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public ItemCarrito getItemCarritoById(Long itemId, Usuario usuarioActual) {

        ItemCarrito itemCarrito = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() -> new ItemCarritoNotFoundException(itemId));

        OwnershipGuard.verificar(usuarioActual, itemCarrito.getCarrito().getUsuario().getId());

        return itemCarrito;
    }


    // Crea el item del carrito con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public ItemCarrito createItemCarrito(ItemCarritoRequest request, Usuario usuarioActual) {

        Carrito carrito = carritoRepository
                .findById(request.getCarrito_id())
                .orElseThrow(() -> new CarritoNotFoundException(request.getCarrito_id()));

        // No se puede agregar un item al carrito de otro usuario.
        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        Marco marco = marcoRepository
                .findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Variante variante = varianteRepository
                .findById(request.getVariante_id())
                .orElseThrow(() -> new VarianteNotFoundException(request.getVariante_id()));

        ItemCarrito itemCarrito = ItemCarrito.builder()
                .carrito(carrito)
                .marco(marco)
                .variante(variante)
                .cantidad(request.getCantidad())
                .build();

        return itemCarritoRepository.save(itemCarrito);
    }


    // Actualiza el item del carrito: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public ItemCarrito updateItemCarrito(
            Long itemId,
            ItemCarritoRequest request,
            Usuario usuarioActual) {

        ItemCarrito itemCarrito = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() -> new ItemCarritoNotFoundException(itemId));

        OwnershipGuard.verificar(usuarioActual, itemCarrito.getCarrito().getUsuario().getId());

        Carrito carrito = carritoRepository
                .findById(request.getCarrito_id())
                .orElseThrow(() -> new CarritoNotFoundException(request.getCarrito_id()));

        // El item tampoco puede pasarse al carrito de otro usuario.
        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        Marco marco = marcoRepository
                .findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Variante variante = varianteRepository
                .findById(request.getVariante_id())
                .orElseThrow(() -> new VarianteNotFoundException(request.getVariante_id()));

        itemCarrito.setCarrito(carrito);
        itemCarrito.setMarco(marco);
        itemCarrito.setVariante(variante);
        itemCarrito.setCantidad(request.getCantidad());

        return itemCarritoRepository.save(itemCarrito);
    }


    // Elimina el item del carrito de la base.
    @Override
    public void deleteItemCarrito(Long itemId, Usuario usuarioActual) {

        ItemCarrito itemCarrito = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() -> new ItemCarritoNotFoundException(itemId));

        OwnershipGuard.verificar(usuarioActual, itemCarrito.getCarrito().getUsuario().getId());

        itemCarritoRepository.delete(itemCarrito);
    }
}
