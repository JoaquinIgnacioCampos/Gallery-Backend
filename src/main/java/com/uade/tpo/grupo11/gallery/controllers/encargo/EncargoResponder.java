package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.repositories.NombreDeUsuario;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Arma las respuestas de los encargos. Existe por el nombre del cliente: pedirlo a la entidad Usuario cargaria
// sus relaciones (carrito, perfil de artista) de a una por cada encargo. En cambio los nombres de toda una
// pagina se piden juntos, en una sola consulta que no carga entidades.
@Component
public class EncargoResponder {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public EncargoResponse armarRespuesta(Encargo encargo) {
        return armarRespuestas(List.of(encargo)).get(0);
    }

    public Page<EncargoResponse> armarRespuesta(Page<Encargo> encargos) {
        List<EncargoResponse> respuestas = armarRespuestas(encargos.getContent());
        // La pagina ya viene armada: se conserva su paginado y solo cambia el contenido.
        return new PageImpl<>(respuestas, encargos.getPageable(), encargos.getTotalElements());
    }

    private List<EncargoResponse> armarRespuestas(List<Encargo> encargos) {
        List<Long> idsDeClientes = encargos.stream().map(encargo -> encargo.getUsuario().getId()).distinct().toList();

        Map<Long, String> nombreCompletoPorIdDeCliente = new HashMap<>();
        for (NombreDeUsuario cliente : usuarioRepository.nombresPorIds(idsDeClientes)) {
            nombreCompletoPorIdDeCliente.put(cliente.id(), cliente.nombre() + " " + cliente.apellido());
        }

        return encargos.stream()
                .map(encargo -> EncargoResponse.fromEntity(
                        encargo, nombreCompletoPorIdDeCliente.get(encargo.getUsuario().getId())))
                .toList();
    }
}
