package com.uade.tpo.grupo11.gallery.services.encargo;

import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.EncargoRepository;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
import com.uade.tpo.grupo11.gallery.repositories.TamanioLienzoRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Logica de negocio de los encargos: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class EncargoServiceImpl implements EncargoService {

    @Autowired
    private EncargoRepository encargoRepository;
    @Autowired
    private PerfilArtistaRepository artistaRepository;
    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;
    @Autowired
    private MarcoRepository marcoRepository;

    // Busca el encargo por id. Solo pueden verlo el cliente y el artista del encargo.
    @Override
    public Encargo getEncargoById(Long id, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(id)
                .orElseThrow(() -> new EncargoNotFoundException(id));

        OwnershipGuard.verificar(usuarioLogueado,
                encargo.getUsuario().getId(), encargo.getArtista().getUsuario().getId());

        return encargo;
    }

    // Devuelve los encargos del artista. Solo el propio artista (o ADMIN) puede listarlos.
    @Override
    public List<Encargo> getEncargosByArtista(Long artistaId, Usuario usuarioLogueado) {
        PerfilArtista artista = artistaRepository.findById(artistaId)
                .orElseThrow(() -> new PerfilArtistaNotFoundException(artistaId));

        OwnershipGuard.verificar(usuarioLogueado, artista.getUsuario().getId());

        return encargoRepository.findByArtistaId(artistaId);
    }

    // Devuelve los encargos del usuario. Solo el propio usuario (o ADMIN) puede listarlos.
    @Override
    public List<Encargo> getEncargosByUsuario(Long usuarioId, Usuario usuarioLogueado) {
        OwnershipGuard.verificar(usuarioLogueado, usuarioId);

        return encargoRepository.findByUsuarioId(usuarioId);
    }

    // Crea el encargo con los datos del request. El cliente sale del usuario logueado,
    // no del body: si viniera ahi, cualquiera podria pedir un encargo a nombre de otro.
    @Override
    public Encargo createEncargo(EncargoRequest request, Usuario usuarioLogueado) {
        PerfilArtista artista = artistaRepository.findById(request.getArtista_id())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(request.getArtista_id()));

        if (!artista.isAcepta_encargos()) {
            throw new PerfilArtistaNoAceptaEncargosException(artista.getId());
        }

        TamanioLienzo tamanio = tamanioLienzoRepository.findById(request.getTamanio_id())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getTamanio_id()));
        Marco marco = marcoRepository.findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Encargo encargo = new Encargo();
        encargo.setArtista(artista);
        encargo.setUsuario(usuarioLogueado);
        encargo.setTamanio(tamanio);
        encargo.setMarco(marco);
        encargo.setTipo_pintura(request.getTipo_pintura());
        encargo.setTipo_lienzo(request.getTipo_lienzo());
        encargo.setEstado_encargo(EstadoEncargo.PENDIENTE);
        encargo.setDescripcion_encargo(request.getDescripcion_encargo());

        return encargoRepository.save(encargo);
    }
    // Mueve el encargo al estado siguiente si la transicion es valida.
    @Override
    public Encargo cambiarEstado(Long encargoId, EstadoEncargo nuevoEstado, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        // Chequeo de pertenencia: el rol ya se valido en el SecurityConfig, pero eso no alcanza.
        // Sin esto, cualquier artista podria cancelar los encargos de otro.
        PerfilArtista artistaLogueado = artistaRepository.findByUsuarioId(usuarioLogueado.getId())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(usuarioLogueado.getId()));

        if (!encargo.getArtista().getId().equals(artistaLogueado.getId())) {
            throw new AccesoDenegadoException(
                    "El encargo con id " + encargoId + " no te pertenece");
        }

        EstadoEncargo estadoActual = encargo.getEstado_encargo();

        if (!transicionesValidas(estadoActual).contains(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(estadoActual, nuevoEstado);
        }

        encargo.setEstado_encargo(nuevoEstado);
        return encargoRepository.save(encargo);
    }

    // Dice a que estados se puede pasar desde el actual. Terminado y cancelado son finales.
    private List<EstadoEncargo> transicionesValidas(EstadoEncargo estadoActual) {
        return switch (estadoActual) {
            case PENDIENTE -> List.of(EstadoEncargo.EN_PROCESO, EstadoEncargo.CANCELADO);
            case EN_PROCESO -> List.of(EstadoEncargo.TERMINADO, EstadoEncargo.CANCELADO);
            case TERMINADO, CANCELADO -> List.of(); // estados finales, no admiten cambios
        };
    }

}
