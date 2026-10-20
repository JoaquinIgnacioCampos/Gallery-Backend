package com.uade.tpo.grupo11.gallery.services.variante;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.ObraEliminadaException;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ListaVaciaException;
import com.uade.tpo.grupo11.gallery.exceptions.TamanioLienzoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteDuplicadaException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.TamanioLienzoRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

// Logica de negocio de las variantes: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class VarianteServiceImpl implements VarianteService {

    @Autowired
    private VarianteRepository repoVariante;

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private TamanioLienzoRepository tamanioLienzoRepository;


    // Devuelve las variantes de las obras en venta. Las de una obra fuera de venta o eliminada
    // solo se consultan por id o filtrando por esa obra.
    @Override
    public Page<Variante> getVariantes(PageRequest pageable) {
        Page<Variante> variantes = repoVariante.findDelCatalogo(pageable);

        if (variantes.isEmpty()) {
            throw new ListaVaciaException("No hay variantes registradas en el catálogo");
        }

        return variantes;
    }

    // Devuelve las variantes de la obra.
    @Override
    public Page<Variante> getVariantesByObra(Long obraId, PageRequest pageable) {
        if (!obraRepository.existsById(obraId)) {
            throw new ObraNotFoundException(obraId);
        }

        Page<Variante> variantes = repoVariante.findByObraId(obraId, pageable);

        if (variantes.isEmpty()) {
            throw new ListaVaciaException("La obra con ID " + obraId + " no posee variantes asociadas");
        }

        return variantes;
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
        validarObraVigente(obra);

        TamanioLienzo tamanio = tamanioLienzoRepository
                .findById(request.getId_tamanio())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getId_tamanio()));

        // Una obra tiene una sola variante por tamanio, aunque el pedido no venga de la pantalla.
        if (repoVariante.existsByObraIdAndTamanioId(obra.getId(), tamanio.getId())) {
            throw new VarianteDuplicadaException(obra.getId(), tamanio.getNombre_tamanio());
        }

        validarPrecio(request.getPrecio_variante());
        validarStock(request.getStock_variante());
        validarDescuento(request.getPorcentaje_descuento());
        validarFechaDescuento(request.getDescuento_hasta());

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
        validarObraVigente(varianteExistente.getObra());

        TamanioLienzo tamanio = tamanioLienzoRepository
                .findById(request.getId_tamanio())
                .orElseThrow(() -> new TamanioLienzoNotFoundException(request.getId_tamanio()));

        // Al cambiar de tamanio no se puede pisar el de otra variante de la misma obra.
        if (repoVariante.existsByObraIdAndTamanioIdAndIdNot(
                varianteExistente.getObra().getId(), tamanio.getId(), varianteId)) {
            throw new VarianteDuplicadaException(varianteExistente.getObra().getId(), tamanio.getNombre_tamanio());
        }

        validarPrecio(request.getPrecio_variante());
        validarStock(request.getStock_variante());
        validarDescuento(request.getPorcentaje_descuento());
        // Un descuento ya vencido que se deja como esta no bloquea otros cambios: solo se valida una fecha nueva.
        if (!Objects.equals(request.getDescuento_hasta(), varianteExistente.getDescuento_hasta())) {
            validarFechaDescuento(request.getDescuento_hasta());
        }

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
        validarObraVigente(variante.getObra());

        validarStock(nuevoStock);

        variante.setStock_variante(nuevoStock);

        return repoVariante.save(variante);
    }


    // Elimina la variante de la base.
    @Override
    public void deleteVariante(Long varianteId, Usuario usuarioActual) {

        Variante variante = getVarianteById(varianteId);

        OwnershipGuard.verificar(usuarioActual, variante.getObra().getArtista().getUsuario().getId());
        validarObraVigente(variante.getObra());

        repoVariante.delete(variante);
    }


    // Las variantes de una obra eliminada quedan como registro: no se agregan, editan ni borran.
    private void validarObraVigente(Obra obra) {
        if (obra.isEliminada()) {
            throw new ObraEliminadaException(obra.getId());
        }
    }


    // Reglas de negocio: viven en el Service, no en el Controller ni en el Repository.
    private void validarPrecio(BigDecimal precio) {

        if (precio == null || precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
    }


    private void validarStock(Integer stock) {

        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }


    // Regla de negocio: un descuento no puede terminar antes de hoy (hoy inclusive es valido). Sin fecha, no vence.
    private void validarFechaDescuento(LocalDate descuentoHasta) {

        if (descuentoHasta != null && descuentoHasta.isBefore(LocalDate.now(Variante.ZONA))) {
            throw new IllegalArgumentException("La fecha del descuento (" + descuentoHasta
                    + ") ya pasó: tiene que ser hoy o una fecha futura");
        }
    }


    // Regla de negocio: el porcentaje tiene que estar entre 0 y 100.
    private void validarDescuento(Integer porcentaje) {

        if (porcentaje != null && (porcentaje < 0 || porcentaje > 100)) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
    }
}
