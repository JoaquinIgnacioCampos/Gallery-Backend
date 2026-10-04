package com.uade.tpo.grupo11.gallery.controllers.usuario;

import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.controllers.carrito.CarritoResponse;
import com.uade.tpo.grupo11.gallery.controllers.compra.CompraResponse;
import com.uade.tpo.grupo11.gallery.controllers.encargo.EncargoResponse;
import com.uade.tpo.grupo11.gallery.controllers.mensaje.MensajeResponse;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaResponse;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;
import com.uade.tpo.grupo11.gallery.services.compra.CompraService;
import com.uade.tpo.grupo11.gallery.services.encargo.EncargoService;
import com.uade.tpo.grupo11.gallery.services.perfilartista.PerfilArtistaService;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import com.uade.tpo.grupo11.gallery.services.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los usuarios y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CarritoService carritoService;
    @Autowired
    private PerfilArtistaService perfilArtistaService;
    @Autowired
    private CompraService compraService;
    @Autowired
    private MensajeService mensajeService;
    @Autowired
    private EncargoService encargoService;

    // Devuelve los usuarios.
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> getUsuarios(@AuthenticationPrincipal Usuario usuarioActual) {
        List<UsuarioResponse> result = usuarioService.getUsuarios(usuarioActual).stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // Crea el usuario con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioRequest usuario_request) {
        Usuario result = usuarioService.createUsuario(usuario_request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + result.getId()))
                .body(UsuarioResponse.fromEntity(result));
    }

    // Devuelve el usuario dueño del token. El front lo necesita apenas se loguea: el token
    // solo lleva el email, pero para pedir su carrito o sus compras hace falta el id.
    @GetMapping("/me")
    public ResponseEntity<PerfilCompletoResponse> getUsuarioActual(@AuthenticationPrincipal Usuario usuarioLogueado) {
        Usuario usuario = usuarioService.getUsuarioActual(usuarioLogueado);

        PerfilArtista perfilArtista = perfilArtistaService
                .getPerfilArtistaByUsuarioOptional(usuarioLogueado.getId())
                .orElse(null);

        return ResponseEntity.ok(PerfilCompletoResponse.of(usuario, perfilArtista));
    }

    // Busca el usuario por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @GetMapping("/{usuario_id}")
    public ResponseEntity<UsuarioResponse> getUsuario(
            @PathVariable("usuario_id") Long usuario_id,
            @AuthenticationPrincipal Usuario usuarioActual) {
        return ResponseEntity.ok(UsuarioResponse.fromEntity(usuarioService.getUsuario(usuario_id, usuarioActual)));
    }

    // Actualiza el usuario: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @PatchMapping("/{usuario_id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @PathVariable("usuario_id") Long usuario_id,
            @Valid @RequestBody UsuarioRequest usuario_request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        Usuario result = usuarioService.updateUsuario(usuario_id, usuario_request, usuarioLogueado);
        return ResponseEntity.ok(UsuarioResponse.fromEntity(result));
    }

    // Devuelve los usuarios.
    @GetMapping("/{usuario_id}/carrito")
    public ResponseEntity<CarritoResponse> getCarrito(
            @PathVariable("usuario_id") Long usuario_id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        Carrito result = carritoService.getOrCreateCarritoByUsuario(usuario_id, usuarioLogueado);
        return ResponseEntity.ok(CarritoResponse.fromEntity(result));
    }

    // Devuelve los usuarios.
    @GetMapping("/{usuario_id}/perfil-artista")
    public ResponseEntity<PerfilArtistaResponse> getPerfilArtista(@PathVariable("usuario_id") Long usuario_id) {
        PerfilArtista result = perfilArtistaService.getPerfilArtistaByUsuario(usuario_id);
        return ResponseEntity.ok(PerfilArtistaResponse.fromEntity(result));
    }

    // Crea el perfil propio usando la identidad autenticada por el filtro JWT.
    @PostMapping("/me/perfil-artista")
    public ResponseEntity<PerfilArtistaResponse> createPerfilArtista(
            @AuthenticationPrincipal Usuario usuarioLogueado,
            @Valid @RequestBody PerfilArtistaRequest perfil_artista_request) {
        PerfilArtista result = perfilArtistaService.createPerfilArtista(usuarioLogueado, perfil_artista_request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuarioLogueado.getId() + "/perfil-artista"))
                .body(PerfilArtistaResponse.fromEntity(result));
    }

    // Devuelve los usuarios.
    // solo devuelve a quien hizo la compra a quien le copraron y al ADMIN
    @GetMapping("/{usuario_id}/compras")
    public ResponseEntity<List<CompraResponse>> getCompras(
            @PathVariable("usuario_id") Long usuario_id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<CompraResponse> result = compraService.getComprasByUsuario(usuario_id, usuarioLogueado).stream()
                .map(CompraResponse::fromEntity).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{usuario_id}/compras")
    public ResponseEntity<CompraResponse> addCompra(@PathVariable("usuario_id") Long usuario_id) {
        Compra result = compraService.createCompraForUsuario(usuario_id);
        return ResponseEntity.ok(CompraResponse.fromEntity(result));
    }

    // Devuelve los mensajes enviados por el usuario.
    @GetMapping("/{usuario_id}/mensajes")
    public ResponseEntity<List<MensajeResponse>> getMensajes(
            @PathVariable("usuario_id") Long usuario_id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<MensajeResponse> result = mensajeService.getMensajesByUsuario(usuario_id, usuarioLogueado).stream()
                .map(MensajeResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }


    // Devuelve los encargos que pidio el usuario (como cliente). Solo el propio usuario (o ADMIN).
    @GetMapping("/{usuario_id}/encargos")
    public ResponseEntity<List<EncargoResponse>> getEncargos(
            @PathVariable("usuario_id") Long usuario_id,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<EncargoResponse> result = encargoService.getEncargosByUsuario(usuario_id, usuarioLogueado).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    // Los encargos propios: el id sale del token, no hace falta que el cliente lo sepa.
    @GetMapping("/me/encargos")
    public ResponseEntity<List<EncargoResponse>> getEncargosPropios(
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        List<EncargoResponse> result = encargoService
                .getEncargosByUsuario(usuarioLogueado.getId(), usuarioLogueado).stream()
                .map(EncargoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }


    // ADMINISTRACION DE CUENTAS - solo ADMIN (la restriccion esta en SecurityConfig).
    // Asignacion de permisos: el administrador cambia el rol de una cuenta.
    // PATCH porque se modifica un solo campo; si el rol no existe en el enum, responde 400.
    @PatchMapping("/{usuario_id}/rol")
    public ResponseEntity<UsuarioResponse> asignarRol(
            @PathVariable("usuario_id") Long usuario_id,
            @RequestParam Rol rol) {

        return ResponseEntity.ok(UsuarioResponse.fromEntity(usuarioService.asignarRol(usuario_id, rol)));
    }
}
