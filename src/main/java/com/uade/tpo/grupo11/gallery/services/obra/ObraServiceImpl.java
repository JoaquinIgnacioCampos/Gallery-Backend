package com.uade.tpo.grupo11.gallery.services.obra;

import com.uade.tpo.grupo11.gallery.exceptions.ListaVaciaException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.obra.ObraRequest;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.EstiloRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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


    // Devuelve las obras.
    @Override
    public Page<Obra> getObras(PageRequest pageable) {
        Page<Obra> obra = repoObra.findDelCatalogo(pageable);

        if (obra.isEmpty()) {
            throw new ListaVaciaException("No hay ninguna obra cargada");
        }

        return obra;
    }

    // Busca obras con filtros opcionales y combinables. Valida los datos antes de consultar.
    @Override
    public Page<Obra> buscarConFiltros(Long artistaId, List<Long> estiloId, BigDecimal precioMin, BigDecimal precioMax,
                                       String q, String orden, PageRequest pageable) {
        if (precioMin != null && precioMin.signum() < 0) {
            throw new IllegalArgumentException("El precio minimo no puede ser negativo.");
        }
        if (precioMax != null && precioMax.signum() < 0) {
            throw new IllegalArgumentException("El precio maximo no puede ser negativo.");
        }
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            throw new IllegalArgumentException("El precio minimo no puede superar al maximo.");
        }

        // Orden: recientes (por defecto), menor-precio o mayor-precio.
        int signo;
        if (orden == null || orden.isBlank() || orden.equals("recientes")) {
            signo = 0;
        } else if (orden.equals("menor-precio")) {
            signo = 1;
        } else if (orden.equals("mayor-precio")) {
            signo = -1;
        } else {
            throw new IllegalArgumentException("Orden invalido: usar recientes, menor-precio o mayor-precio.");
        }

        // Texto: sin espacios sobrantes, en minuscula, y con % _ ! escapados para que se busquen literalmente.
        String patron = null;
        if (q != null && !q.isBlank()) {
            String limpio = q.trim().toLowerCase()
                    .replace("!", "!!")
                    .replace("%", "!%")
                    .replace("_", "!_");
            patron = "%" + limpio + "%";
        }

        // Verificar existencia de entidades si se envían los IDs
        if (artistaId != null && !perfilArtistaRepository.existsById(artistaId)) {
            throw new PerfilArtistaNotFoundException(artistaId);
        }
        // Estilos: sin repetidos; cada uno tiene que existir. Lista vacia = sin filtro de estilo.
        List<Long> estiloIds = estiloId == null ? List.of() : estiloId.stream().distinct().toList();
        for (Long unEstilo : estiloIds) {
            if (!estiloRepository.existsById(unEstilo)) {
                throw new RecursoNoEncontradoException("No existe el estilo con ID: " + unEstilo);
            }
        }

        // "Hoy" en Argentina: un descuento vence al terminar su ultimo dia.
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"));

        Page<Obra> resultados = repoObra.buscarConFiltros(
                artistaId, estiloIds.size(), estiloIds.isEmpty() ? List.of(-1L) : estiloIds,
                precioMin, precioMax, patron, hoy, signo, pageable);

        // Verificar si la consulta volvió vacía (el handler responde 204)
        if (resultados.isEmpty()) {
            throw new ListaVaciaException("No se encontraron obras que coincidan con los filtros aplicados.");
        }

        return resultados;
    }


    // Devuelve las obras del artista.
    @Override
    public List<Obra> getObrasByArtista(Long artistaId) {
        if (!perfilArtistaRepository.existsById(artistaId)) {
            throw new PerfilArtistaNotFoundException(artistaId);
        }

        List<Obra> obras = repoObra.findDelCatalogoByArtistaId(artistaId);

        if (obras.isEmpty()) {
            throw new ListaVaciaException("No hay obras publicadas por el artista" + artistaId);
        }

        return obras;
    }


    // Busca la obra por id. Si no existe, se lanza la excepcion y el handler responde 404.
    // Las eliminadas tambien se devuelven: una compra vieja puede necesitar mostrarla.
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
                .orElseThrow(() -> new PerfilArtistaNotFoundException(
                        "Para publicar obras primero tenés que crear tu perfil de artista"));

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

        if (obraExistente.isEliminada()) {
            throw new ObraEliminadaException(obraExistente.getId());
        }

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


    // Da de baja la obra sin borrar la fila: sus variantes pueden estar en facturas o carritos.
    // Queda fuera del catalogo y de la venta, se sigue pudiendo consultar por id y no se modifica mas.
    @Override
    public void deleteObra(Long obraId, Usuario usuarioLogueado) {

        Obra obra = getObraById(obraId);

        OwnershipGuard.verificar(usuarioLogueado, obra.getArtista().getUsuario().getId());

        // Ya dada de baja: no hay nada mas que hacer con ella.
        if (obra.isEliminada()) {
            throw new ObraEliminadaException(obra.getId());
        }

        obra.setEliminada(true);
        obra.setEn_venta(false);

        repoObra.save(obra);
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