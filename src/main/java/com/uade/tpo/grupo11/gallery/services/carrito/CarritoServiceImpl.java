package com.uade.tpo.grupo11.gallery.services.carrito;


import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ItemCarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
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
    public List<Carrito> getCarritos() {

        return carritoRepository.findAll();
    }


    // Busca el carrito por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Carrito getCarritoById(Long carritoId) {

        return carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));
    }


    // Crea el carrito con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Carrito createCarrito(CarritoRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));

        Carrito carrito = Carrito.builder()
                .usuario(usuario)
                .direccion_cliente(request.getDireccion_cliente())
                .build();

        return carritoRepository.save(carrito);
    }


    // Actualiza el carrito: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Carrito updateCarrito(
            Long carritoId,
            CarritoRequest request) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        Usuario usuario = usuarioRepository
                .findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));

        carrito.setUsuario(usuario);
        carrito.setDireccion_cliente(request.getDireccion_cliente());

        return carritoRepository.save(carrito);
    }


    // Devuelve las lineas del carrito: que variante, cuantas unidades y con que marco.
    @Override
    public List<ItemCarrito> getItemsByCarrito(Long carritoId) {

        carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        return itemCarritoRepository.findByCarritoId(carritoId);
    }


    // Saca todos los items del carrito sin borrar el carrito.
    @Override
    @Transactional
    public void vaciarCarrito(Long carritoId) {

        carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        itemCarritoRepository.deleteByCarritoId(carritoId);
    }


    // Devuelve el carrito del usuario y, si todavia no tiene, se lo crea.
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
