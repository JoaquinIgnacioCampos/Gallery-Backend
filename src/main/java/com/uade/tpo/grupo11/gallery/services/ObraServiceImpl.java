package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObraServiceImpl implements ObraService {

    @Autowired
    private ObraRepository repoObra;

    @Override
    public List<Obra> getListObras() {
        return repoObra.findAll();
    }

    @Override
    public Obra getObraById(Long obraId) {
        return repoObra.findById(obraId)
                .orElseThrow(() -> new ObraNotFoundException(obraId));
    }

    @Override
    public Obra createObra(Obra obra) {
        return repoObra.save(obra);
    }

    @Override
    public Obra modificarObra(Long obraId, Obra obra) {
        Obra obraExistente = getObraById(obraId);
        obraExistente.setNombreObra(obra.getNombreObra());
        obraExistente.setDescripcionObra(obra.getDescripcionObra());
        obraExistente.setEnVenta(obra.isEnVenta());
        obraExistente.setArtista(obra.getArtista());
        return repoObra.save(obraExistente);
    }

    @Override
    public void eliminarObra(Long obraId) {
        Obra obra = getObraById(obraId);
        repoObra.delete(obra);
    }
}
