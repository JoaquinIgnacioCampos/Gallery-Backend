package com.uade.tpo.grupo11.gallery.services.perfilartista;

import org.springframework.data.domain.Page;
import com.uade.tpo.grupo11.gallery.services.filtro.FiltroPorEntregaYFecha;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaUpdateRequest;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import com.uade.tpo.grupo11.gallery.entities.Usuario;

import java.util.List;
import java.util.Optional;

// Contrato: que sabe hacer el servicio de los perfiles de artista. La implementacion es la que lleva la logica.
public interface PerfilArtistaService {

    // Devuelve los perfiles de artista del usuario.
    PerfilArtista getPerfilArtistaByUsuario(Long usuarioId);

    // El perfil de artista del usuario logueado, con un mensaje pensado para los endpoints /me.
    PerfilArtista getPerfilArtistaPropio(Usuario usuarioLogueado);

    // Crea el perfil exclusivamente para el usuario autenticado.
    PerfilArtista createPerfilArtista(Usuario usuarioLogueado, PerfilArtistaRequest request);

    // Devuelve los perfiles de artista.
    Page<PerfilArtista> getPerfilArtistas(PageRequest pageable);

    // Busca el perfil de artista por id. Si no existe, se lanza la excepcion y el handler responde 404.
    PerfilArtista getPerfilArtistaById(Long perfilArtistaId);

    // Actualiza el perfil de artista: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    PerfilArtista updatePerfilArtista(Long perfilArtistaId, PerfilArtistaUpdateRequest request, Usuario usuarioLogueado);

    // Devuelve las obras del perfil de artista que ve el publico: solo las que estan en venta.
    Page<Obra> getObrasByPerfilArtista(Long perfilArtistaId, PageRequest pageable);

    // Devuelve las obras que gestiona el propio artista, esten o no en venta, cada una con su resumen
    // (estilos, portada y variantes). Sale de una cantidad fija de consultas, sin importar cuantas obras haya.
    // Filtros opcionales y combinables: texto en el nombre, estilos (alcanza con uno), si esta en venta o no
    // (null = todas) y solo las que tienen alguna variante sin stock.
    Page<ObraPropiaResumen> getObrasPropias(Long perfilArtistaId, String nombreObra, List<Long> estiloId, Boolean enVenta,
                                            boolean sinStock, PageRequest pageable);

    // Devuelve las ventas del artista: las facturas emitidas a su nombre, de la mas nueva a la mas vieja,
    // junto con lo vendido en total. El filtro (fechas y entrega) se aplica a la lista y al total.
    VentasDelArtista getFacturasByPerfilArtista(Long perfilArtistaId, Usuario usuarioLogueado,
                                                FiltroPorEntregaYFecha filtro, PageRequest pageable);

    Page<PerfilArtista> buscarPerfilArtistas(String q, List<Long> estiloId, PageRequest pageable);

    Optional<PerfilArtista> getPerfilArtistaByUsuarioOptional(Long usuarioId);
}
