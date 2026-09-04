package com.uade.tpo.grupo11.gallery.services.Compra;

import com.uade.tpo.grupo11.gallery.controllers.Compra.CompraRequest;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.CompraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ImagenNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraServiceImpl implements CompraService {

    @Autowired
    private CompraRepository repoCompra;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public List<Compra> getListCompras() {
        return repoCompra.findAll();
    }

    @Override
    public Compra getCompraById(Long compraId) {
        return repoCompra.findById(compraId)
                .orElseThrow(() ->
                        new CompraNotFoundException(compraId));
    }



    @Override
    public Compra createCompra(CompraRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        Compra compra = Compra.builder()
                .usuario(usuario)
                .fechaCompra(request.getFechaCompra())
                .totalCompra(request.getTotalCompra())
                .build();

        return repoCompra.save(compra);
    }


    @Override
    public Compra modificarCompra (Long compraId, Compra compra) {

        Compra compra = repoCompra
                .findById(compraId)
                .orElseThrow(() ->
                        new CompraNotFoundException(compraId));

        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new UsuarioNotFoundException(UserId));
        compra.setUsuario(usuario);
        compra.setFechaCompra(request.getFechaCompra());
        compra.setTotalCompra(request.getTotalCompra());

        return repoCompra.save(compra);
    }


    @Override
    public void eliminarCompra(Long compraId) {

        Compra compra = repoCompra
                .findById(compraId);

        repoCompra.delete(compra);
    }}


