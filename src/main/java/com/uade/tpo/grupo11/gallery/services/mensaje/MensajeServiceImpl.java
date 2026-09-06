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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeServiceImpl implements MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    //cualquier cosa se cambian los nombres para coincidir con las clases futuras >:)
    @Autowired
    private EncargoRepository encargoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;



    @Override
    public Mensaje getMensajeById(Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new MensajeNotFoundException(id));
    }

    @Override
    public List<Mensaje> getMensajesByEncargo(Long encargoId) {
        return mensajeRepository.findByEncargoId(encargoId);
    }

    @Override
    public List<Mensaje> getMensajesByUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        return mensajeRepository.findByEmisorId(usuarioId);
    }

    @Override
    public Mensaje createMensaje(Long encargoId, Long usuarioEmisorId, String contenido) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));
        Usuario emisor = usuarioRepository.findById(usuarioEmisorId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioEmisorId));

        Mensaje mensaje = new Mensaje();
        mensaje.setEncargo(encargo);
        mensaje.setEmisor(emisor);
        mensaje.setContenido_mensaje(contenido);

        return mensajeRepository.save(mensaje);
    }

}
