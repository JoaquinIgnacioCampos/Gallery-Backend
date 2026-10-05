package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.controllers.paginacion.Paginacion;
import org.springframework.data.domain.Page;
import com.uade.tpo.grupo11.gallery.controllers.mensaje.MensajeResponse;
import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.encargo.EncargoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los encargos y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/encargos")
public class EncargoController {

    @Autowired
    private EncargoService encargoService;

    @Autowired
    private MensajeService mensajeService;

    // Busca el encargo por id. Solo lo pueden ver el cliente y el artista del encargo.
    @GetMapping("/{id}")
    public ResponseEntity<EncargoResponse> getEncargoById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(encargoService.getEncargoById(id, usuarioLogueado)));
    }

    // Los encargos del artista y del usuario se piden desde /api/artistas/{id}/encargos
    // y /api/usuarios/{id}/encargos respectivamente, junto con el resto de los recursos
    // propios de cada uno (facturas, compras, mensajes, etc.).

    // Crea el encargo con los datos del request. El cliente sale del usuario logueado.
    @PostMapping
    public ResponseEntity<EncargoResponse> createEncargo(
            @Valid @RequestBody EncargoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        Encargo result = encargoService.createEncargo(request, usuarioLogueado);
        return ResponseEntity.created(URI.create("/api/encargos/" + result.getId()))
                .body(EncargoResponse.fromEntity(result));
    }
    // Devuelve los mensajes del encargo. Solo el cliente y el artista del encargo.
    @GetMapping("/{encargoId}/mensajes")
    public ResponseEntity<Page<MensajeResponse>> getMensajesByEncargo(
            @PathVariable Long encargoId,
            @AuthenticationPrincipal Usuario usuarioLogueado,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(mensajeService.getMensajesByEncargo(encargoId, usuarioLogueado, Paginacion.de(page, size))
                .map(MensajeResponse::fromEntity));
    }

    // Solo el artista DUENIO del encargo puede moverle el estado.
    // El rol lo filtra el SecurityConfig; la pertenencia la verifica el service.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EncargoResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {

        return ResponseEntity.ok(EncargoResponse.fromEntity(
                encargoService.cambiarEstado(id, request.getNuevoEstado(), usuarioLogueado)));
    }

    // El artista carga o cambia el precio. Cualquier cambio deja el precio pendiente de aceptacion del cliente.
    @PatchMapping("/{id}/precio")
    public ResponseEntity<EncargoResponse> definirPrecio(
            @PathVariable Long id,
            @Valid @RequestBody DefinirPrecioRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(
                encargoService.definirPrecio(id, request.getPrecio_acordado(), usuarioLogueado)));
    }

    // El cliente acepta el precio cargado: el encargo queda aprobado y el precio congelado.
    @PostMapping("/{id}/aceptar-precio")
    public ResponseEntity<EncargoResponse> aceptarPrecio(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(
                encargoService.aceptarPrecio(id, usuarioLogueado)));
    }

    // El cliente paga el encargo terminado. El pago es simulado desde el front; el cambio a PAGADO lo hace el back.
    @PostMapping("/{id}/pagar")
    public ResponseEntity<EncargoResponse> pagarEncargo(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        return ResponseEntity.ok(EncargoResponse.fromEntity(
                encargoService.pagarEncargo(id, usuarioLogueado)));
    }

}
