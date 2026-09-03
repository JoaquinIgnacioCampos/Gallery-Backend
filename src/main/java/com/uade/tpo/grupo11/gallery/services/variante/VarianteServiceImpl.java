package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VarianteServiceImpl implements VarianteService {
    @Autowired
    private VarianteRepository repoVariante;


    @Override
    public List<Variante> getListVariante() {
        return repoVariante.findAll();

    }

    @Override
    public Variante getVarianteById(Long varianteId) {
        return repoVariante.findById(varianteId)
                .orElseThrow(() -> new VarianteNotFoundException(varianteId));
    }

    @Override
    public Variante createVariante(Variante variante) {
        return repoVariante.save(variante);
    }

    @Override
    public Variante modificarVariante(Long varianteId, Variante variante) {

        Variante varianteExistente = getVarianteById(varianteId);

        varianteExistente.setObra(variante.getObra());
        varianteExistente.setTamanioLienzo(variante.getTamanioLienzo());
        varianteExistente.setPrecioVariante(variante.getPrecioVariante());
        varianteExistente.setStockVariante(variante.getStockVariante());
        varianteExistente.setPorcentajeDescuento(variante.getPorcentajeDescuento());
        varianteExistente.setDescuentoHasta(variante.getDescuentoHasta());

        return repoVariante.save(varianteExistente);
    }

    @Override
    public void eliminarVariante(Long varianteId) {
        Variante variante = getVarianteById(varianteId);
        //Se comprueba existencia de Obra
        repoVariante.delete(variante);

    }
}

