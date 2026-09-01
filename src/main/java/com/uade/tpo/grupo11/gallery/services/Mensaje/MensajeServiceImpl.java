package com.uade.tpo.grupo11.gallery.services.Mensaje;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import com.uade.tpo.grupo11.gallery.exceptions.EncargoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.MensajeNotFoundException;
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
    public Mensaje obtenerPorId(Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new MensajeNotFoundException(id));
    }

    @Override
    public List<Mensaje> obtenerPorEncargo(Long encargoId) {
        return mensajeRepository.findByEncargoId(encargoId);
    }

    @Override
    public Mensaje enviarMensaje(Long encargoId, Long usuarioEmisorId, String contenido) {
        Encargo encargo = encargoRepository.findById(encargoId)
                .orElseThrow(() -> new EncargoNotFoundException(encargoId));
        Usuario emisor = usuarioRepository.findById(usuarioEmisorId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioEmisorId));

        Mensaje mensaje = new Mensaje();
        mensaje.setEncargo(encargo);
        mensaje.setUsuarioEmisor(emisor);
        mensaje.setContenidoMensaje(contenido);

        return mensajeRepository.save(mensaje);
    }

}
