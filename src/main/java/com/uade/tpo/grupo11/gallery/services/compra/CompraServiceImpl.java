package com.uade.tpo.grupo11.gallery.services.compra;

import com.uade.tpo.grupo11.gallery.controllers.compra.CompraRequest;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.CompraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.RecursoNoEncontradoException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.CompraRepository;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;

import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

// Logica de negocio de las compras: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class CompraServiceImpl implements CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    // Devuelve las compras.
    @Override
    public List<Compra> getCompras(Usuario usuarioActual) {
        OwnershipGuard.soloAdmin(usuarioActual);

        List<Compra> compras = compraRepository.findAll();
        if (compras.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay compras registradas en el sistema");
        }
        return compras;
    }


    // Busca la compra por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Compra getCompraById(Long compraId, Usuario usuarioActual) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new CompraNotFoundException(compraId));

        List<Long> artistasInvolucrados = facturaRepository.findByCompraId(compraId).stream()
                .map(f -> f.getArtista().getUsuario().getId())
                .toList();

        Long[] propietarios = java.util.stream.Stream.concat(
                java.util.stream.Stream.of(compra.getUsuario().getId()),
                artistasInvolucrados.stream()
        ).toArray(Long[]::new);

        OwnershipGuard.verificar(usuarioActual, propietarios);

        return compra;
    }

    // Atajo para "mis compras", igual que /me en Usuario.
    @Override
    public List<Compra> getMisCompras(Usuario usuarioActual) {
        List<Compra> compras = compraRepository.findByUsuarioId(usuarioActual.getId());
        if (compras.isEmpty()) {
            throw new RecursoNoEncontradoException("No tenés compras registradas todavía");
        }
        return compras;
    }

    // Crea la compra con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Compra createCompra(CompraRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.getUsuario_id())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuario_id()));

        Compra compra = Compra.builder()
                .usuario(usuario)
                .fecha_compra(request.getFecha_compra())
                .total_compra(BigDecimal.ZERO)
                .build();

        return compraRepository.save(compra);
    }


    // Actualiza la compra: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
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

        return compraRepository.save(compra);
    }


    // Elimina la compra de la base.
    @Override
    public void deleteCompra(Long compraId) {

        Compra compra = compraRepository
                .findById(compraId)
                .orElseThrow(() -> new CompraNotFoundException(compraId));

        compraRepository.delete(compra);
    }

    // Devuelve las compras del usuario.
    @Override
    public List<Compra> getComprasByUsuario(Long usuarioId, Usuario usuarioActual) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        OwnershipGuard.verificar(usuarioActual, usuarioId);

        List<Compra> compras = compraRepository.findByUsuarioId(usuarioId);
        if (compras.isEmpty()) {
            throw new RecursoNoEncontradoException("El usuario con id " + usuarioId + " no tiene compras registradas");
        }
        return compras;
    }

    // Crea una compra vacia para un usuario. El checkout usa su propio camino.
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
