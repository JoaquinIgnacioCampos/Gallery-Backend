package com.uade.tpo.grupo11.gallery.services.carrito;


import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ItemCarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.grupo11.gallery.controllers.carrito.CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;


    @Override
    public List<Carrito> getCarritos(Usuario usuarioActual) {

        // Ver todos los carritos es una vista administrativa: no es de nadie en particular.
        OwnershipGuard.soloAdmin(usuarioActual);

        return carritoRepository.findAll();
    }


    @Override
    public Carrito getCarritoById(Long carritoId, Usuario usuarioActual) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        return carrito;
    }


    @Override
    public Carrito createCarrito(CarritoRequest request, Usuario usuarioActual) {

        Usuario usuario = usuarioRepository
                .findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));

        // No se puede crear un carrito a nombre de otro usuario.
        OwnershipGuard.verificar(usuarioActual, usuario.getId());

        Carrito carrito = Carrito.builder()
                .usuario(usuario)
                .direccion_cliente(request.getDireccion_cliente())
                .build();

        return carritoRepository.save(carrito);
    }


    @Override
    public Carrito updateCarrito(
            Long carritoId,
            CarritoRequest request,
            Usuario usuarioActual) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        Usuario usuario = usuarioRepository
                .findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));

        carrito.setUsuario(usuario);
        carrito.setDireccion_cliente(request.getDireccion_cliente());

        return carritoRepository.save(carrito);
    }


    @Override
    public List<ItemCarrito> getItemsByCarrito(Long carritoId, Usuario usuarioActual) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        return itemCarritoRepository.findByCarritoId(carritoId);
    }


    @Override
    @Transactional
    public void vaciarCarrito(Long carritoId, Usuario usuarioActual) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        itemCarritoRepository.deleteByCarritoId(carritoId);
    }


    @Override
    public Carrito getOrCreateCarritoByUsuario(Long usuarioId) {

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        return carritoRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(
                        Carrito.builder()
                                .usuario(usuario)
                                .build()
                ));
    }
}
