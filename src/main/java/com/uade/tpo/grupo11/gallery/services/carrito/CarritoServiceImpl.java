package com.uade.tpo.grupo11.gallery.services.carrito;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoVacioException;
import com.uade.tpo.grupo11.gallery.exceptions.RecursoNoEncontradoException;
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

// Logica de negocio de los carritos: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;


    // Devuelve los carritos.
    @Override
    public Page<Carrito> getCarritos(Usuario usuarioActual, PageRequest pageable) {
        // Ver todos los carritos es una vista administrativa: no es de nadie en particular.
        OwnershipGuard.soloAdmin(usuarioActual);

        Page<Carrito> carritos = carritoRepository.findAll(pageable);
        if (carritos.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay carritos registrados en el sistema");
        }
        return carritos;
    }



    // Busca el carrito por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Carrito getCarritoById(Long carritoId, Usuario usuarioActual) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        return carrito;
    }


    // Crea el carrito con los datos del request. El dueño es siempre el usuario logueado.
    @Override
    public Carrito createCarrito(CarritoRequest request, Usuario usuarioActual) {

        Carrito carrito = Carrito.builder()
                .usuario(usuarioActual)
                .direccion_cliente(request.getDireccion_cliente())
                .build();

        return carritoRepository.save(carrito);
    }


    // Actualiza el carrito: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Carrito updateCarrito(
            Long carritoId,
            CarritoRequest request,
            Usuario usuarioActual) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        // El dueño del carrito no se cambia al editarlo, igual que el artista de una obra.
        carrito.setDireccion_cliente(request.getDireccion_cliente());

        return carritoRepository.save(carrito);
    }


    // Devuelve las lineas del carrito: que variante, cuantas unidades y con que marco.
    @Override
    public Page<ItemCarrito> getItemsByCarrito(Long carritoId, Usuario usuarioActual, PageRequest pageable) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        OwnershipGuard.verificar(usuarioActual, carrito.getUsuario().getId());

        Page<ItemCarrito> items = itemCarritoRepository.findByCarritoId(carritoId, pageable);

        if (items.isEmpty()) {
            throw new CarritoVacioException(carritoId);
        }

        return items;
    }


    // Saca todos los items del carrito sin borrar el carrito.
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
    public Carrito getOrCreateCarritoByUsuario(Long usuarioId, Usuario usuarioActual) {
        return getOrCreateCarritoByUsuario(usuarioId, usuarioActual, null);
    }


    @Override
    public Carrito getOrCreateCarritoByUsuario(Long usuarioId, Usuario usuarioActual, String direccionInicial) {

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        OwnershipGuard.verificar(usuarioActual, usuarioId);

        return carritoRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(
                        Carrito.builder()
                                .usuario(usuario)
                                .direccion_cliente(direccionInicial)
                                .build()
                ));
    }
}
