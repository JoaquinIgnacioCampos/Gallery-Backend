package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.controllers.obra.ObraRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.EstiloRepository;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

// Logica de negocio de las obras: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class ObraServiceImpl implements ObraService {

    @Autowired
    private ObraRepository repoObra;

    @Autowired
    private PerfilArtistaRepository perfilArtistaRepository;

    @Autowired
    private EstiloRepository estiloRepository;

    // Los usamos solo para saber si la obra tiene hijos antes de borrarla.
    @Autowired
    private VarianteRepository varianteRepository;

    @Autowired
    private ImagenRepository imagenRepository;


    // Devuelve las obras.
    @Override
    public List<Obra> getObras() {
        List<Obra> obra = repoObra.findAll();

        if (obra.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay ninguna obra cargada");
        }

        return obra;
    }

    // Busca obras con filtros opcionales y combinables. Valida los precios antes de consultar.
    @Override
    public List<Obra> buscarConFiltros(Long artistaId, Long estiloId, BigDecimal precioMin, BigDecimal precioMax) {
        if (precioMin != null && precioMin.signum() < 0) {
            throw new IllegalArgumentException("El precio minimo no puede ser negativo.");
        }
        if (precioMax != null && precioMax.signum() < 0) {
            throw new IllegalArgumentException("El precio maximo no puede ser negativo.");
        }
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            throw new IllegalArgumentException("El precio minimo no puede superar al maximo.");
        }

        // Verificar existencia de entidades si se envían los IDs
        if (artistaId != null && !perfilArtistaRepository.existsById(artistaId)) {
            throw new PerfilArtistaNotFoundException(artistaId);
        }
        if (estiloId != null && !estiloRepository.existsById(estiloId)) {
            throw new RecursoNoEncontradoException("No existe el estilo con ID: " + estiloId);
        }

        List<Obra> resultados = repoObra.buscarConFiltros(artistaId, estiloId, precioMin, precioMax);

        // Verificar si la consulta volvió vacía
        if (resultados.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron obras que coincidan con los filtros aplicados.");
        }

        return resultados;
    }


    // Devuelve las obras del artista.
    @Override
    public List<Obra> getObrasByArtista(Long artistaId) {
        if (!perfilArtistaRepository.existsById(artistaId)) {
            throw new PerfilArtistaNotFoundException(artistaId);
        }

        List<Obra> obras = repoObra.findByArtistaId(artistaId);

        if (obras.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay obras publicadas por el artista" + artistaId);
        }

        return obras;
    }


    // Busca la obra por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Obra getObraById(Long obraId) {
        return repoObra.findById(obraId)
                .orElseThrow(() -> new ObraNotFoundException(obraId));
    }


    // Crea la obra con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Obra createObra(ObraRequest request, Usuario usuarioLogueado) {

        // El artista sale del usuario logueado y no de un id del body: si no, cualquiera
        // podria publicar a nombre de otro. 404 si todavia no creo su perfil de artista.
        PerfilArtista artista = perfilArtistaRepository
                .findByUsuarioId(usuarioLogueado.getId())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(usuarioLogueado.getId()));

        Obra obra = Obra.builder()
                .nombre_obra(request.getNombre_obra())
                .descripcion_obra(request.getDescripcion_obra())
                .en_venta(Boolean.TRUE.equals(request.getEn_venta()))
                .artista(artista)
                .estilos(buscarEstilos(request.getEstilo_ids()))
                .build();

        return repoObra.save(obra);
    }


    // Actualiza la obra: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Obra updateObra(Long obraId, ObraRequest request, Usuario usuarioLogueado) {

        Obra obraExistente = getObraById(obraId);

        OwnershipGuard.verificar(usuarioLogueado, obraExistente.getArtista().getUsuario().getId());

        obraExistente.setNombre_obra(request.getNombre_obra());
        obraExistente.setDescripcion_obra(request.getDescripcion_obra());
        obraExistente.setEn_venta(Boolean.TRUE.equals(request.getEn_venta()));

        // El artista de una obra no se cambia al editarla: la autoria no se transfiere.
        // Los estilos si se pueden reasignar, pero solo si el cliente los mando.
        if (request.getEstilo_ids() != null) {
            obraExistente.setEstilos(buscarEstilos(request.getEstilo_ids()));
        }

        return repoObra.save(obraExistente);
    }


    // Elimina la obra de la base.
    @Override
    public void deleteObra(Long obraId, Usuario usuarioLogueado) {

        Obra obra = getObraById(obraId);

        OwnershipGuard.verificar(usuarioLogueado, obra.getArtista().getUsuario().getId());

        // Si la obra todavia tiene variantes o imagenes, la base rechazaria el borrado
        // por la clave foranea. Avisamos con un 409 y un mensaje claro.
        boolean tieneVariantes = !varianteRepository.findByObraId(obraId).isEmpty();
        boolean tieneImagenes = !imagenRepository.findByObraIdOrdenadas(obraId).isEmpty();

        if (tieneVariantes || tieneImagenes) {
            throw new ObraEnUsoException(obraId);
        }

        repoObra.delete(obra);
    }




    private Set<Estilo> buscarEstilos(Set<Long> estiloIds) {

        if (estiloIds == null || estiloIds.isEmpty()) {
            throw new RecursoNoEncontradoException("Debe indicar al menos un estilo para la obra");
        }

        Set<Estilo> estilos = new HashSet<>();

        for (Long estiloId : estiloIds) {
            Estilo estilo = estiloRepository
                    .findById(estiloId)
                    .orElseThrow(() -> new EstiloNotFoundException(estiloId));
            estilos.add(estilo);
        }

        return estilos;
    }
}
