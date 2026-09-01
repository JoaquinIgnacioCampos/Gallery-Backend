package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.List;

public interface ObraService {
    List<Obra> getListObras();
    Obra getObraById(Long obraId);
    Obra createObra(Obra obra);
    Obra modificarObra(Long obraId, Obra obra);
    void eliminarObra(Long obraId);
}
