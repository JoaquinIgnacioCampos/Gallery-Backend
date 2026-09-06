package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.controllers.obra.ObraRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.List;

// Contrato: que sabe hacer el servicio de obras. La logica vive en ObraServiceImpl.
public interface ObraService {
    List<Obra> getObras();
    List<Obra> getObrasByArtista(Long artistaId);
    Obra getObraById(Long obraId);
    Obra createObra(ObraRequest request);
    Obra updateObra(Long obraId, ObraRequest request);
    void deleteObra(Long obraId);
}
