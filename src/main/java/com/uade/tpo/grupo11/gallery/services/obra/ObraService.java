package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.controllers.obra.ObraRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;
import java.math.BigDecimal;
import java.util.Set;

// Contrato: que sabe hacer el servicio de obras. La logica vive en ObraServiceImpl.
public interface ObraService {
    List<Obra> getObras();
    List<Obra> buscarConFiltros(Long artistaId, Long estiloId, BigDecimal precioMin, BigDecimal precioMax);
    List<Obra> getObrasByArtista(Long artistaId);
    Obra getObraById(Long obraId);
    // Recibe al usuario logueado: la obra se publica a nombre de quien la crea.
    Obra createObra(ObraRequest request, Usuario usuarioLogueado);
    Obra updateObra(Long obraId, ObraRequest request);
    void deleteObra(Long obraId);


}
