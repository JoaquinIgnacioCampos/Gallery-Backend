package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.controllers.obra.ObraRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.EstiloNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ObraEnUsoException;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.EstiloRepository;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

// SERVICE: aca vive la logica de negocio de las obras. Valida que el artista y los estilos
// existan, controla los filtros y no deja borrar una obra que tiene hijos.
// @Service va en la implementacion, no en la interfaz: es esta clase la que Spring instancia
// como bean y le inyecta a quien la pida.
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


    @Override
    public List<Obra> getObras() {
        return repoObra.findAll();
    }

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
        return repoObra.buscarConFiltros(artistaId, estiloId, precioMin, precioMax);
    }


    @Override
    public List<Obra> getObrasByArtista(Long artistaId) {
        return repoObra.findByArtistaId(artistaId);
    }


    @Override
    public Obra getObraById(Long obraId) {
        return repoObra.findById(obraId)
                .orElseThrow(() -> new ObraNotFoundException(obraId));
    }


    @Override
    public Obra createObra(ObraRequest request, Usuario usuarioLogueado) {

        // El artista sale del usuario logueado, NO de un id que mande el cliente.
        // Si lo tomaramos del body, cualquier artista podria publicar a nombre de otro.
        // 404 si el usuario todavia no creo su perfil de artista.
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


    @Override
    public Obra updateObra(Long obraId, ObraRequest request) {

        Obra obraExistente = getObraById(obraId);

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


    @Override
    public void deleteObra(Long obraId) {

        Obra obra = getObraById(obraId);

        // Si la obra todavia tiene variantes o imagenes, la base rechazaria el borrado
        // por la clave foranea. Avisamos con un 409 y un mensaje claro.
        boolean tieneVariantes = !varianteRepository.findByObraId(obraId).isEmpty();
        boolean tieneImagenes = !imagenRepository.findByObraIdOrdenadas(obraId).isEmpty();

        if (tieneVariantes || tieneImagenes) {
            throw new ObraEnUsoException(obraId);
        }

        repoObra.delete(obra);
    }




    // Convierte la lista de ids que manda el cliente en las entidades Estilo.
    private Set<Estilo> buscarEstilos(Set<Long> estiloIds) {

        Set<Estilo> estilos = new HashSet<>();

        if (estiloIds == null) {
            return estilos;
        }

        for (Long estiloId : estiloIds) {
            Estilo estilo = estiloRepository
                    .findById(estiloId)
                    .orElseThrow(() -> new EstiloNotFoundException(estiloId));
            estilos.add(estilo);
        }

        return estilos;
    }
}
