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
    public List<Variante> getVariantes() {
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
    public Variante updateVariante(Long varianteId, Variante variante) {

        Variante varianteExistente = getVarianteById(varianteId);

        varianteExistente.setObra(variante.getObra());
        varianteExistente.setTamanio(variante.getTamanio());
        varianteExistente.setPrecio_variante(variante.getPrecio_variante());
        varianteExistente.setStock_variante(variante.getStock_variante());
        varianteExistente.setPorcentaje_descuento(variante.getPorcentaje_descuento());
        varianteExistente.setDescuento_hasta(variante.getDescuento_hasta());

        return repoVariante.save(varianteExistente);
    }

    @Override
    public void deleteVariante(Long varianteId) {
        Variante variante = getVarianteById(varianteId);
        //Se comprueba existencia de Obra
        repoVariante.delete(variante);

    }
}

