package com.uade.tpo.grupo11.gallery.controllers.usuario;

import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.controllers.carrito.CarritoResponse;
import com.uade.tpo.grupo11.gallery.controllers.compra.CompraResponse;
import com.uade.tpo.grupo11.gallery.controllers.mensaje.MensajeResponse;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaResponse;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;
import com.uade.tpo.grupo11.gallery.services.compra.CompraService;
import com.uade.tpo.grupo11.gallery.services.perfilartista.PerfilArtistaService;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import com.uade.tpo.grupo11.gallery.services.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> getUsuarios() {
        List<UsuarioResponse> result = usuarioService.getUsuarios().stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioRequest usuario_request) {
        Usuario result = usuarioService.createUsuario(usuario_request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + result.getId()))
                .body(UsuarioResponse.fromEntity(result));
    }

    @GetMapping("/{usuario_id}")
    public ResponseEntity<UsuarioResponse> getUsuario(@PathVariable("usuario_id") Long usuario_id) {
        return ResponseEntity.ok(UsuarioResponse.fromEntity(usuarioService.getUsuario(usuario_id)));
    }

    @PatchMapping("/{usuario_id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable("usuario_id") Long usuario_id, @Valid @RequestBody UsuarioRequest usuario_request) {
        Usuario result = usuarioService.updateUsuario(usuario_id, usuario_request);
        return ResponseEntity.ok(UsuarioResponse.fromEntity(result));
    }

    @GetMapping("/{usuario_id}/carrito")
    public ResponseEntity<CarritoResponse> getCarrito(@PathVariable("usuario_id") Long usuario_id) {
        Carrito result = carritoService.getOrCreateCarritoByUsuario(usuario_id);
        return ResponseEntity.ok(CarritoResponse.fromEntity(result));
    }

    @GetMapping("/{usuario_id}/perfil-artista")
    public ResponseEntity<PerfilArtistaResponse> getPerfilArtista(@PathVariable("usuario_id") Long usuario_id) {
        PerfilArtista result = perfilArtistaService.getPerfilArtistaByUsuario(usuario_id);
        return ResponseEntity.ok(PerfilArtistaResponse.fromEntity(result));
    }

    @PostMapping("/{usuario_id}/perfil-artista")
    public ResponseEntity<PerfilArtistaResponse> createPerfilArtista(
            @PathVariable("usuario_id") Long usuario_id,
            @Valid @RequestBody PerfilArtistaRequest perfil_artista_request) {
        PerfilArtista result = perfilArtistaService.createPerfilArtista(usuario_id, perfil_artista_request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuario_id + "/perfil-artista"))
                .body(PerfilArtistaResponse.fromEntity(result));
    }

    @GetMapping("/{usuario_id}/compras")
    public ResponseEntity<List<CompraResponse>> getCompras(@PathVariable("usuario_id") Long usuario_id) {
        List<CompraResponse> result = compraService.getComprasByUsuario(usuario_id).stream()
                .map(CompraResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{usuario_id}/compras")
    public ResponseEntity<CompraResponse> addCompra(@PathVariable("usuario_id") Long usuario_id) {
        Compra result = compraService.createCompraForUsuario(usuario_id);
        return ResponseEntity.ok(CompraResponse.fromEntity(result));
    }

    @GetMapping("/{usuario_id}/mensajes")
    public ResponseEntity<List<MensajeResponse>> getMensajes(@PathVariable("usuario_id") Long usuario_id) {
        List<MensajeResponse> result = mensajeService.getMensajesByUsuario(usuario_id).stream()
                .map(MensajeResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }
}
