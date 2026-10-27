package com.uade.tpo.grupo11.gallery.services.perfilartista;

import com.uade.tpo.grupo11.gallery.exceptions.ListaVaciaException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaUpdateRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;
import com.uade.tpo.grupo11.gallery.services.filtro.FiltroPorEntregaYFecha;
import com.uade.tpo.grupo11.gallery.services.filtro.TextoDeBusqueda;
import com.uade.tpo.grupo11.gallery.exceptions.*;
import com.uade.tpo.grupo11.gallery.repositories.FacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemFacturaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.ResumenDeVariantes;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import com.uade.tpo.grupo11.gallery.repositories.VentaDeArtista;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Logica de negocio de los perfiles de artista: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class PerfilArtistaServiceImpl implements PerfilArtistaService {

    @Autowired
    private PerfilArtistaRepository perfilArtistaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ObraRepository obraRepository;
    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private ItemFacturaRepository itemFacturaRepository;
    @Autowired
    private VarianteRepository varianteRepository;
    @Autowired
    private ImagenRepository imagenRepository;

    // Devuelve los perfiles de artista del usuario.
    @Override
    public PerfilArtista getPerfilArtistaByUsuario(Long usuarioId) {
        return perfilArtistaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new PerfilArtistaNotFoundException(
                        "El usuario con id " + usuarioId + " no tiene perfil de artista"));
    }

    // El perfil de artista del usuario logueado, para los endpoints /me.
    @Override
    public PerfilArtista getPerfilArtistaPropio(Usuario usuarioLogueado) {
        return perfilArtistaRepository.findByUsuarioId(usuarioLogueado.getId())
                .orElseThrow(() -> new PerfilArtistaNotFoundException("Todavía no creaste tu perfil de artista"));
    }

    //no tira excepcion si no existe,
    // para componer el perfil completo sin que la ausencia de perfil artista corte el flujo.
    @Override
    public Optional<PerfilArtista> getPerfilArtistaByUsuarioOptional(Long usuarioId) {
        return perfilArtistaRepository.findByUsuarioId(usuarioId);
    }

    // La identidad viene del contexto de seguridad, nunca de un id elegido por el cliente.
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public PerfilArtista createPerfilArtista(Usuario usuarioLogueado, PerfilArtistaRequest request) {
        Long usuarioId = usuarioLogueado.getId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        if (usuario.getRol_usuario() == Rol.ADMIN) {
            throw new AccesoDenegadoException("Como administrador ya tenés todos los permisos: no necesitás un perfil de artista");
        }

        if (perfilArtistaRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new DuplicatePerfilArtistaException();
        }

        PerfilArtista perfilArtista = new PerfilArtista();
        perfilArtista.setUsuario(usuario);
        perfilArtista.setNombre_artistico(validateNombreArtistico(request.getNombre_artistico()));
        perfilArtista.setAcepta_encargos(request.isAcepta_encargos());
        perfilArtista = perfilArtistaRepository.save(perfilArtista);

        if (usuario.getRol_usuario() == Rol.CLIENTE) {
            usuario.setRol_usuario(Rol.ARTISTA_CLIENTE);
            usuarioRepository.save(usuario);
        }

        return perfilArtista;
    }

    // Devuelve los perfiles de artista.
    @Override
    public Page<PerfilArtista> getPerfilArtistas(PageRequest pageable) {
        Page<PerfilArtista> perfiles = perfilArtistaRepository.findAll(pageable);

        if (perfiles.isEmpty()) {
            throw new ListaVaciaException("No se encontraron perfiles de artista registrados");
        }

        return perfiles;
    }

    // Busca artistas por nombre artistico y por estilos de sus obras publicadas (alguno de los elegidos).
    // Sin resultados: 204 (ListaVaciaException).
    @Override
    public Page<PerfilArtista> buscarPerfilArtistas(String q, List<Long> estiloId, PageRequest pageable) {
        // Texto en minuscula y con % _ ! escapados para que se busquen literalmente.
        String patron = null;
        if (q != null && !q.isBlank()) {
            patron = "%" + q.trim().toLowerCase()
                    .replace("!", "!!")
                    .replace("%", "!%")
                    .replace("_", "!_") + "%";
        }

        // Estilos: sin repetidos. Lista vacia = sin filtro (se manda un id de relleno que no se usa).
        List<Long> estiloIds = estiloId == null ? List.of() : estiloId.stream().distinct().toList();

        Page<PerfilArtista> perfiles = perfilArtistaRepository.buscar(
                patron, estiloIds.size(), estiloIds.isEmpty() ? List.of(-1L) : estiloIds, pageable);

        if (perfiles.isEmpty()) {
            throw new ListaVaciaException("No se encontraron artistas que coincidan con la busqueda");
        }

        return perfiles;
    }

    // Busca el perfil de artista por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public PerfilArtista getPerfilArtistaById(Long perfilArtistaId) {
        return perfilArtistaRepository.findById(perfilArtistaId)
                .orElseThrow(() -> new PerfilArtistaNotFoundException(perfilArtistaId));
    }

    // Actualiza el perfil de artista: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public PerfilArtista updatePerfilArtista(Long perfilArtistaId, PerfilArtistaUpdateRequest request, Usuario usuarioLogueado) {
        PerfilArtista perfilArtista = getPerfilArtistaById(perfilArtistaId);

        OwnershipGuard.verificar(usuarioLogueado, perfilArtista.getUsuario().getId());

        if (request.getAcepta_encargos() != null) {
            perfilArtista.setAcepta_encargos(request.getAcepta_encargos());
        }
        if (request.getNombre_artistico() != null) {
            perfilArtista.setNombre_artistico(validateNombreArtistico(request.getNombre_artistico()));
        }

        return perfilArtistaRepository.save(perfilArtista);
    }

    // Devuelve las obras del perfil de artista que ve el publico: solo las que estan en venta.
    @Override
    public Page<Obra> getObrasByPerfilArtista(Long perfilArtistaId, PageRequest pageable) {
        getPerfilArtistaById(perfilArtistaId);
        Page<Obra> obras = obraRepository.findDelCatalogoByArtistaId(perfilArtistaId, pageable);

        if (obras.isEmpty()) {
            throw new ListaVaciaException("El artista con ID " + perfilArtistaId + " no tiene obras en venta");
        }

        return obras;
    }

    // Devuelve las obras que gestiona el propio artista: incluye las que no estan en venta, no las eliminadas.
    // Los estilos, la portada y las variantes de toda la pagina se piden de una vez (una consulta cada uno,
    // con IN) y se reparten en memoria: no hay una consulta por obra. Una obra sin fotos o sin variantes
    // no corta la lista (para eso no se usan los services de imagen y variante, que lanzan ListaVaciaException).
    @Override
    @Transactional(readOnly = true)
    public Page<ObraPropiaResumen> getObrasPropias(Long perfilArtistaId, String nombreObra, List<Long> estiloId, Boolean enVenta,
                                                   boolean sinStock, PageRequest pageable) {
        getPerfilArtistaById(perfilArtistaId);

        // Estilos: sin repetidos; una lista vacia = sin filtro de estilo (IN () no es valido: se manda un id que no existe).
        List<Long> estiloIds = estiloId == null ? List.of() : estiloId.stream().distinct().toList();
        // En venta: 0 todas, 1 solo las que estan en venta, 2 solo las que no.
        int estadoDeVenta = enVenta == null ? 0 : (enVenta ? 1 : 2);

        // La consulta ordena por id: asi las paginas no repiten ni omiten obras.
        Page<Obra> obras = obraRepository.buscarPropias(perfilArtistaId, estadoDeVenta, estiloIds.size(),
                estiloIds.isEmpty() ? List.of(-1L) : estiloIds, TextoDeBusqueda.patron(nombreObra), sinStock, pageable);

        if (obras.isEmpty()) {
            throw new ListaVaciaException("Todavía no tenés obras publicadas");
        }

        List<Long> ids = new ArrayList<>();
        obras.forEach(obra -> ids.add(obra.getId()));

        Map<Long, List<Long>> estilosPorObra = new HashMap<>();
        obraRepository.findEstilosDeObras(ids).forEach(fila ->
                estilosPorObra.computeIfAbsent(fila.getObraId(), clave -> new ArrayList<>()).add(fila.getEstiloId()));

        Map<Long, ResumenDeVariantes> variantesPorObra = new HashMap<>();
        varianteRepository.resumirPorObras(ids).forEach(fila -> variantesPorObra.put(fila.getObraId(), fila));

        Map<Long, String> portadaPorObra = new HashMap<>();
        imagenRepository.findPortadasDeObras(ids).forEach(fila -> portadaPorObra.putIfAbsent(fila.getObraId(), fila.getUrl()));

        return obras.map(obra -> {
            ResumenDeVariantes variantes =variantesPorObra.get(obra.getId());
            return new ObraPropiaResumen(
                    obra,
                    estilosPorObra.getOrDefault(obra.getId(), List.of()),
                    portadaPorObra.get(obra.getId()),
                    variantes == null ? 0 : variantes.getCantidad(),
                    variantes == null ? 0 : variantes.getSinStock()
            );
        });
    }

    // Devuelve las ventas del artista. Son datos de plata, asi que no alcanza con tener
    // el rol: hay que ser el dueño de ese perfil (o ADMIN), y eso lo decide el token.
    @Override
    public VentasDelArtista getFacturasByPerfilArtista(Long perfilArtistaId, Usuario usuarioLogueado,
                                                       FiltroPorEntregaYFecha filtro, PageRequest pageable) {
        PerfilArtista perfil = getPerfilArtistaById(perfilArtistaId);
        OwnershipGuard.verificar(usuarioLogueado, perfil.getUsuario().getId());

        Page<VentaDeArtista> facturas = facturaRepository.findVentasDelArtista(perfilArtistaId, filtro.desde(),
                filtro.hasta(), filtro.filtroDeEntrega(), TipoEntrega.ENVIO_DOMICILIO, TipoEntrega.RETIRO_LOCAL, pageable);

        if (facturas.isEmpty()) {
            throw new ListaVaciaException(OwnershipGuard.esPropio(usuarioLogueado, perfil.getUsuario().getId())
                    ? "Todavía no tenés ventas registradas"
                    : "El artista con ID " + perfilArtistaId + " no registra ventas ni facturas");
        }

        // La cantidad de obras de toda la pagina se pide de una vez (una consulta con IN): no hay una por factura.
        List<Long> ids = new ArrayList<>();
        facturas.forEach(factura -> ids.add(factura.getId()));

        Map<Long, Long> obrasPorFactura = new HashMap<>();
        itemFacturaRepository.contarObrasPorFacturas(ids).forEach(fila -> obrasPorFactura.put(fila.getFacturaId(), fila.getCantidad()));

        Page<VentaResumen> ventas = facturas.map(factura ->
                new VentaResumen(factura, obrasPorFactura.getOrDefault(factura.getId(), 0L)));

        return new VentasDelArtista(ventas, facturaRepository.sumarVentasDelArtista(perfilArtistaId, filtro.desde(),
                filtro.hasta(), filtro.filtroDeEntrega(), TipoEntrega.ENVIO_DOMICILIO, TipoEntrega.RETIRO_LOCAL));
    }

    // Regla de negocio: el nombre artistico no puede venir vacio.
    private String validateNombreArtistico(String nombreArtistico) {
        if (nombreArtistico == null || nombreArtistico.isBlank()) {
            throw new PerfilArtistaInvalidDataException("El nombre artístico es obligatorio");
        }
        if (nombreArtistico.length() > 150) {
            throw new PerfilArtistaInvalidDataException(
                    "El nombre artístico no puede superar los 150 caracteres"
            );
        }
        return nombreArtistico.trim();
    }
}