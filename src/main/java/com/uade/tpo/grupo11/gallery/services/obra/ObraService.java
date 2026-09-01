package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.entities.Obra;

import java.util.List;

// Contrato: qué sabe hacer el servicio de obras. La lógica vive en ObraServiceImpl.
public interface ObraService {
    Obra getObraById(Long obraId);

    List<Obra> getListObras();

    Obra createObra(Obra obra);

    Obra modificarObra(Long ObraId, Obra obra);

    void eliminarObra(Long obraId);
}