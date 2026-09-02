package com.uade.tpo.grupo11.gallery.services.Compra;

import com.uade.tpo.grupo11.gallery.controllers.Compra.CompraRequest;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraServiceImpl implements CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public List<Compra> getCompras() {

        return compraRepository.findAll();
    }


    @Override
    public Compra getCompraById(Long compraId) {

        return compraRepository
                .findById(compraId)
                .orElseThrow(() ->
                        new RuntimeException("Compra no encontrada"));
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

        return compraRepository.save(compra);
    }


    @Override
    public Compra updateCompra(
            Long compraId,
            CompraRequest request) {

        Compra compra = compraRepository
                .findById(compraId)
                .orElseThrow(() ->
                        new RuntimeException("Compra no encontrada"));

        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        compra.setUsuario(usuario);
        compra.setFechaCompra(request.getFechaCompra());
        compra.setTotalCompra(request.getTotalCompra());

        return compraRepository.save(compra);
    }


    @Override
    public void deleteCompra(Long compraId) {

        Compra compra = compraRepository
                .findById(compraId)
                .orElseThrow(() ->
                        new RuntimeException("Compra no encontrada"));

        compraRepository.delete(compra);
    }
}
