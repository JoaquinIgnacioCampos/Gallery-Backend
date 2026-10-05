package com.uade.tpo.grupo11.gallery.services.encargo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoRequest;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;

import java.util.List;

// Contrato: que sabe hacer el servicio de los encargos. La implementacion es la que lleva la logica.
public interface EncargoService {
    // Busca el encargo por id. Solo lo pueden ver el cliente y el artista involucrados.
    Encargo getEncargoById(Long id, Usuario usuarioLogueado);
    // Devuelve los encargos del artista. Solo el propio artista (o ADMIN) puede pedirlos.
    Page<Encargo> getEncargosByArtista(Long artistaId, Usuario usuarioLogueado, PageRequest pageable);
    // Devuelve los encargos del usuario. Solo el propio usuario (o ADMIN) puede pedirlos.
    Page<Encargo> getEncargosByUsuario(Long usuarioId, Usuario usuarioLogueado, PageRequest pageable);
    // Crea el encargo con los datos del request. El cliente sale del usuario logueado.
    Encargo createEncargo(EncargoRequest request, Usuario usuarioLogueado);
    // Recibe al usuario logueado para verificar que el encargo sea suyo.
    Encargo cambiarEstado(Long encargoId, EstadoEncargo nuevoEstado, Usuario usuarioLogueado);
    // El cliente paga el encargo terminado; el back lo pasa a PAGADO.
    Encargo pagarEncargo(Long encargoId, Usuario usuarioLogueado);
    // El artista carga o cambia el precio mientras el encargo este pendiente.
    Encargo definirPrecio(Long encargoId, java.math.BigDecimal precio, Usuario usuarioLogueado);
    // El cliente acepta el precio cargado por el artista: el encargo queda aprobado y el precio congelado.
    Encargo aceptarPrecio(Long encargoId, Usuario usuarioLogueado);

}
