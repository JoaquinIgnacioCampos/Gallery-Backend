package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// @Service: acá sí va. Spring crea este objeto y se lo inyecta a quien pida un ObraService.
@Service
public class ObraServiceImpl implements ObraService {

    @Autowired
    private ObraRepository repoObra;

    @Override
    public Obra getObraById(Long obraId) {
        // findById devuelve Optional: o está, o lanzamos el error con nombre propio.
        return repoObra.findById(obraId)
                .orElseThrow(() -> new ObraNotFoundException(obraId));
    }
}