package com.uade.tpo.grupo11.gallery.services.carrito;


import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.grupo11.gallery.controllers.carrito.CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public List<Carrito> getCarritos() {

        return carritoRepository.findAll();
    }


    @Override
    public Carrito getCarritoById(Long carritoId) {

        return carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));
    }


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


    @Override
    public void deleteCarrito(Long carritoId) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() -> new CarritoNotFoundException(carritoId));

        carritoRepository.delete(carrito);
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
