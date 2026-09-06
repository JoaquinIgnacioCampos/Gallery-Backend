package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.controllers.obra.ObraRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
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
import java.util.List;
import java.util.Set;

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
    public List<Obra> getObrasByArtista(Long artistaId) {
        return repoObra.findByArtistaId(artistaId);
    }


    @Override
    public Obra getObraById(Long obraId) {
        return repoObra.findById(obraId)
                .orElseThrow(() -> new ObraNotFoundException(obraId));
    }


    @Override
    public Obra createObra(ObraRequest request) {

        // Verificamos que el artista exista antes de guardar: asi devolvemos 404
        // en lugar de un 500 por violacion de clave foranea.
        PerfilArtista artista = perfilArtistaRepository
                .findById(request.getArtista_id())
                .orElseThrow(() -> new PerfilArtistaNotFoundException(request.getArtista_id()));

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
