package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
//Conecta con el Repository
public interface ObraService {
    Obra getObraById(Long obra_id);
}
