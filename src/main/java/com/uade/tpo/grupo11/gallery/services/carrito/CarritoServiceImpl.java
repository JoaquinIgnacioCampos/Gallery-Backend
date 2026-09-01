package com.uade.tpo.grupo11.gallery.services.carrito;


import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.grupo11.gallery.controllers.Carrito.CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;
// falta el import...UsuarioRepository;
// import el import...Usuario;

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
                .orElseThrow(() ->
                        new RuntimeException("Carrito no encontrado"));
    }


    @Override
    public Carrito createCarrito(CarritoRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        Carrito carrito = Carrito.builder()
                .usuario(usuario)
                .direccionCliente(request.getDireccionCliente())
                .build();

        return carritoRepository.save(carrito);
    }


    @Override
    public Carrito updateCarrito(
            Long carritoId,
            CarritoRequest request) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() ->
                        new RuntimeException("Carrito no encontrado"));

        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        carrito.setUsuario(usuario);
        carrito.setDireccionCliente(request.getDireccionCliente());

        return carritoRepository.save(carrito);
    }


    @Override
    public void deleteCarrito(Long carritoId) {

        Carrito carrito = carritoRepository
                .findById(carritoId)
                .orElseThrow(() ->
                        new RuntimeException("Carrito no encontrado"));

        carritoRepository.delete(carrito);
    }
}
