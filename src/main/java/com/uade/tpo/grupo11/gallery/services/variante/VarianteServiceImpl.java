package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.TamanioLienzoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.TamanioLienzoRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// SERVICE: la logica de negocio de las variantes. Verifica que la obra y el tamanio existan
// antes de guardar, y valida stock y descuento. Esas reglas no van en el Controller (que solo
// traduce HTTP) ni en el Repository (que solo lee y escribe): van aca.
@Service
public class VarianteServiceImpl implements VarianteService {

    @Autowired
    private VarianteRepository repoVariante;

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;


    @Override
    public List<Variante> getVariantes() {
        return repoVariante.findAll();
    }


    @Override
    public List<Variante> getVariantesByObra(Long obraId) {

        // Si la obra no existe avisamos con 404, en lugar de devolver una lista vacia
        // que el cliente podria interpretar como "esta obra no tiene variantes".
        if (!obraRepository.existsById(obraId)) {
            throw new ObraNotFoundException(obraId);
        }

        return repoVariante.findByObraId(obraId);
    }


    @Override
    public Variante getVarianteById(Long varianteId) {
        return repoVariante.findById(varianteId)
                .orElseThrow(() -> new VarianteNotFoundException(varianteId));
    }


    @Override
    public Variante createVariante(VarianteRequest request) {

        Obra obra = obraRepository
                .findById(request.getObra_id())
                .orElseThrow(() -> new ObraNotFoundException(request.getObra_id()));

        TamanioLienzo tamanio = tamanioLienzoRepository
                .findById(request.getId_tamanio())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getId_tamanio()));

        validarStock(request.getStock_variante());
        validarDescuento(request.getPorcentaje_descuento());

        Variante variante = Variante.builder()
                .obra(obra)
                .tamanio(tamanio)
                .precio_variante(request.getPrecio_variante())
                .stock_variante(request.getStock_variante())
                .porcentaje_descuento(request.getPorcentaje_descuento())
                .descuento_hasta(request.getDescuento_hasta())
                .build();

        return repoVariante.save(variante);
    }


    @Override
    public Variante updateVariante(Long varianteId, VarianteRequest request) {

        Variante varianteExistente = getVarianteById(varianteId);

        TamanioLienzo tamanio = tamanioLienzoRepository
                .findById(request.getId_tamanio())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getId_tamanio()));

        validarStock(request.getStock_variante());
        validarDescuento(request.getPorcentaje_descuento());

        // La obra de una variante no se cambia: una variante nace colgada de su obra.
        varianteExistente.setTamanio(tamanio);
        varianteExistente.setPrecio_variante(request.getPrecio_variante());
        varianteExistente.setStock_variante(request.getStock_variante());
        varianteExistente.setPorcentaje_descuento(request.getPorcentaje_descuento());
        varianteExistente.setDescuento_hasta(request.getDescuento_hasta());

        return repoVariante.save(varianteExistente);
    }


    @Override
    public Variante actualizarStock(Long varianteId, Integer nuevoStock) {

        Variante variante = getVarianteById(varianteId);

        validarStock(nuevoStock);

        variante.setStock_variante(nuevoStock);

        return repoVariante.save(variante);
    }


    @Override
    public void deleteVariante(Long varianteId) {

        Variante variante = getVarianteById(varianteId);

        repoVariante.delete(variante);
    }


    // Reglas de negocio: viven en el Service, no en el Controller ni en el Repository.
    private void validarStock(Integer stock) {

        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }


    private void validarDescuento(Integer porcentaje) {

        if (porcentaje != null && (porcentaje < 0 || porcentaje > 100)) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
    }
}
