package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Obra;

// Contrato: qué sabe hacer el servicio de obras. La lógica vive en ObraServiceImpl.
public interface ObraService {
    Obra getObraById(Long obraId);
}