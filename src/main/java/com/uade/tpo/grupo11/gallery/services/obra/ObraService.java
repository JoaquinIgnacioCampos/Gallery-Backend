package com.uade.tpo.grupo11.gallery.services.obra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.obra.ObraRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;
import java.math.BigDecimal;
import java.util.Set;

// Contrato: que sabe hacer el servicio de obras. La logica vive en ObraServiceImpl.
public interface ObraService {
    // Devuelve las obras.
    Page<Obra> getObras(PageRequest pageable);
    // Busca obras con filtros opcionales y combinables. Valida los precios antes de consultar.
    Page<Obra> buscarConFiltros(Long artistaId, Long estiloId, BigDecimal precioMin, BigDecimal precioMax, PageRequest pageable);
    // Devuelve las obras del artista.
    List<Obra> getObrasByArtista(Long artistaId);
    // Busca la obra por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Obra getObraById(Long obraId);
    // Recibe al usuario logueado: la obra se publica a nombre de quien la crea.
    Obra createObra(ObraRequest request, Usuario usuarioLogueado);
    Obra updateObra(Long obraId, ObraRequest request, Usuario usuarioLogueado);
    void deleteObra(Long obraId, Usuario usuarioLogueado);
}
