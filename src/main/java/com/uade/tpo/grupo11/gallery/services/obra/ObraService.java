package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.List;

public interface ObraService {
    List<Obra> getObras();
    Obra getObraById(Long obraId);
    Obra createObra(Obra obra);
    Obra updateObra(Long obraId, Obra obra);
    void deleteObra(Long obraId);
}
