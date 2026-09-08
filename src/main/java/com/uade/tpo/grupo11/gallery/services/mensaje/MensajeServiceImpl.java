package com.uade.tpo.grupo11.gallery.services.mensaje;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.EncargoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.MensajeNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
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
        OwnershipGuard.verificar(usuarioLogueado,
                encargo.getUsuario().getId(), encargo.getArtista().getUsuario().getId());

        return mensaje;
    }

    // Devuelve los mensajes del encargo. Solo el cliente y el artista del encargo.
    @Override
    public List<Mensaje> getMensajesByEncargo(Long encargoId, Usuario usuarioLogueado) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        OwnershipGuard.verificar(usuarioLogueado,
                encargo.getUsuario().getId(), encargo.getArtista().getUsuario().getId());

        return mensajeRepository.findByEncargoId(encargoId);
    }

    // Devuelve los mensajes enviados por el usuario. Solo el propio usuario (o ADMIN).
    @Override
    public List<Mensaje> getMensajesByUsuario(Long usuarioId, Usuario usuarioLogueado) {
        usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        OwnershipGuard.verificar(usuarioLogueado, usuarioId);

        return mensajeRepository.findByEmisorId(usuarioId);
    }

    // Crea el mensaje. El emisor es siempre el usuario logueado, y tiene que ser
    // participante del encargo (el cliente o el artista) para poder escribir ahi.
    @Override
    public Mensaje createMensaje(Long encargoId, Usuario usuarioLogueado, String contenido) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));

        OwnershipGuard.verificar(usuarioLogueado,
                encargo.getUsuario().getId(), encargo.getArtista().getUsuario().getId());

        Mensaje mensaje = new Mensaje();
        mensaje.setEncargo(encargo);
        mensaje.setEmisor(usuarioLogueado);
        mensaje.setContenido_mensaje(contenido);

        return mensajeRepository.save(mensaje);
    }

}
