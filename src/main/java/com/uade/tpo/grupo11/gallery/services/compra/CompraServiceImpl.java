package com.uade.tpo.grupo11.gallery.services.compra;

import com.uade.tpo.grupo11.gallery.controllers.compra.CompraRequest;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.CompraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
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
                .orElseThrow(() -> new CompraNotFoundException(compraId));
    }


    @Override
    public Compra createCompra(CompraRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));

        Compra compra = Compra.builder()
                .usuario(usuario)
                .fecha_compra(request.getFecha_compra())
                .total_compra(request.getTotal_compra())
                .build();

        return compraRepository.save(compra);
    }


    @Override
    public Compra updateCompra(
            Long compraId,
            CompraRequest request) {

        Compra compra = compraRepository
                .findById(compraId)
                .orElseThrow(() -> new CompraNotFoundException(compraId));

        Usuario usuario = usuarioRepository
                .findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));

        compra.setUsuario(usuario);
        compra.setFecha_compra(request.getFecha_compra());
        compra.setTotal_compra(request.getTotal_compra());

        return compraRepository.save(compra);
    }


    @Override
    public void deleteCompra(Long compraId) {

        Compra compra = compraRepository
                .findById(compraId)
                .orElseThrow(() -> new CompraNotFoundException(compraId));

        compraRepository.delete(compra);
    }

    @Override
    public List<Compra> getComprasByUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        return compraRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Compra createCompraForUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        Compra compra = Compra.builder()
                .usuario(usuario)
                .fecha_compra(LocalDateTime.now())
                .total_compra(BigDecimal.ZERO)
                .build();

        return compraRepository.save(compra);
    }
}
