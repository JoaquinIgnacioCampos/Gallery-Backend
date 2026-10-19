package com.uade.tpo.grupo11.gallery.services.mensaje;

import com.uade.tpo.grupo11.gallery.exceptions.ListaVaciaException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.EncargoRepository;
import com.uade.tpo.grupo11.gallery.repositories.MensajeRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Logica de negocio de los mensajes: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class MensajeServiceImpl implements MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    //cualquier cosa se cambian los nombres para coincidir con las clases futuras >:)
    @Autowired
    private EncargoRepository encargoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;



    // Busca el mensaje por id. Solo lo pueden ver el cliente y el artista del encargo.
    @Override
    public Mensaje getMensajeById(Long id, Usuario usuarioLogueado) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new MensajeNotFoundException(id));

        Encargo encargo = mensaje.getEncargo();
        verificarParticipante(encargo, usuarioLogueado);

        return mensaje;
    }

    // Devuelve los mensajes del encargo. Solo el cliente y el artista del encargo.
    @Override
    public Page<Mensaje> getMensajesByEncargo(Long encargoId, Usuario usuarioLogueado, PageRequest pageable) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        verificarParticipante(encargo, usuarioLogueado);

        Page<Mensaje> mensajes = mensajeRepository.findByEncargoIdOrderByFecha_creacion_mensajeDesc(encargoId, pageable);
        if (mensajes.isEmpty()) {
            throw new ListaVaciaException("El encargo con id " + encargoId + " todavía no tiene mensajes");
        }
        return mensajes;
    }

    // Devuelve los mensajes enviados por el propio usuario. ADMIN no accede a conversaciones.
    @Override
    public Page<Mensaje> getMensajesByUsuario(Long usuarioId, Usuario usuarioLogueado, PageRequest pageable) {
        if (usuarioLogueado.getRol_usuario() == Rol.ADMIN || !OwnershipGuard.esPropio(usuarioLogueado, usuarioId)) {
            throw new AccesoDenegadoException("Los mensajes son privados de los participantes");
        }
        usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        Page<Mensaje> mensajes = mensajeRepository.findByEmisorId(usuarioId, pageable);
        if (mensajes.isEmpty()) {
            throw new ListaVaciaException(OwnershipGuard.esPropio(usuarioLogueado, usuarioId)
                    ? "Todavía no enviaste mensajes"
                    : "El usuario con id " + usuarioId + " no envió mensajes todavía");
        }
        return mensajes;
    }

    // Crea el mensaje. El emisor es siempre el usuario logueado, y tiene que ser
    // participante del encargo (el cliente o el artista) para poder escribir ahi.
    @Override
    public Mensaje createMensaje(Long encargoId, Usuario usuarioLogueado, String contenido) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        verificarParticipante(encargo, usuarioLogueado);

        if (encargo.getEstado_encargo() == com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo.CANCELADO) {
            throw new EncargoCanceladoException(encargoId);
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setEncargo(encargo);
        mensaje.setEmisor(usuarioLogueado);
        mensaje.setContenido_mensaje(contenido);

        return mensajeRepository.save(mensaje);
    }
    private void verificarParticipante(Encargo encargo, Usuario usuarioLogueado) {
        if (usuarioLogueado.getRol_usuario() == Rol.ADMIN
                || (!OwnershipGuard.esPropio(usuarioLogueado, encargo.getUsuario().getId())
                && !OwnershipGuard.esPropio(usuarioLogueado, encargo.getArtista().getUsuario().getId()))) {
            throw new AccesoDenegadoException("Los mensajes son privados de los participantes");
        }
    }
}
