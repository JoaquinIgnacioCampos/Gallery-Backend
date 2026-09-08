package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.TamanioLienzoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.TamanioLienzoRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// Logica de negocio de las variantes: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class VarianteServiceImpl implements VarianteService {

    @Autowired
    private VarianteRepository repoVariante;

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;


    // Devuelve las variantes.
    @Override
    public List<Variante> getVariantes() {
        return repoVariante.findAll();
    }


    // Devuelve las variantes de la obra.
    @Override
    public List<Variante> getVariantesByObra(Long obraId) {

        // Si la obra no existe avisamos con 404, en lugar de devolver una lista vacia
        // que el cliente podria interpretar como "esta obra no tiene variantes".
        if (!obraRepository.existsById(obraId)) {
            throw new ObraNotFoundException(obraId);
        }

        return repoVariante.findByObraId(obraId);
    }


    // Busca la variante por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Variante getVarianteById(Long varianteId) {
        return repoVariante.findById(varianteId)
                .orElseThrow(() -> new VarianteNotFoundException(varianteId));
    }


    // Crea la variante con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Variante createVariante(VarianteRequest request, Usuario usuarioActual) {

        Obra obra = obraRepository
                .findById(request.getObra_id())
                .orElseThrow(() -> new ObraNotFoundException(request.getObra_id()));

        OwnershipGuard.verificar(usuarioActual, obra.getArtista().getUsuario().getId());

        TamanioLienzo tamanio = tamanioLienzoRepository
                .findById(request.getId_tamanio())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getId_tamanio()));

        validarPrecio(request.getPrecio_variante());
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


    // Actualiza la variante: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Variante updateVariante(Long varianteId, VarianteRequest request, Usuario usuarioActual) {

        Variante varianteExistente = getVarianteById(varianteId);

        OwnershipGuard.verificar(usuarioActual, varianteExistente.getObra().getArtista().getUsuario().getId());

        TamanioLienzo tamanio = tamanioLienzoRepository
                .findById(request.getId_tamanio())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getId_tamanio()));

        validarPrecio(request.getPrecio_variante());
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


    // Cambia solo el stock. Por eso es PATCH y no PUT.
    @Override
    public Variante actualizarStock(Long varianteId, Integer nuevoStock, Usuario usuarioActual) {

        Variante variante = getVarianteById(varianteId);

        OwnershipGuard.verificar(usuarioActual, variante.getObra().getArtista().getUsuario().getId());

        validarStock(nuevoStock);

        variante.setStock_variante(nuevoStock);

        return repoVariante.save(variante);
    }


    // Elimina la variante de la base.
    @Override
    public void deleteVariante(Long varianteId, Usuario usuarioActual) {

        Variante variante = getVarianteById(varianteId);

        OwnershipGuard.verificar(usuarioActual, variante.getObra().getArtista().getUsuario().getId());

        repoVariante.delete(variante);
    }


    // Reglas de negocio: viven en el Service, no en el Controller ni en el Repository.
    private void validarPrecio(BigDecimal precio) {

        if (precio == null || precio.signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
    }


    private void validarStock(Integer stock) {

        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }


    // Regla de negocio: el porcentaje tiene que estar entre 0 y 100.
    private void validarDescuento(Integer porcentaje) {

        if (porcentaje != null && (porcentaje < 0 || porcentaje > 100)) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
    }
}
