package com.uade.tpo.grupo11.gallery.controllers.usuario;

import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;
import com.uade.tpo.grupo11.gallery.services.compra.CompraService;
import com.uade.tpo.grupo11.gallery.services.perfilartista.PerfilArtistaService;
import com.uade.tpo.grupo11.gallery.services.mensaje.MensajeService;
import com.uade.tpo.grupo11.gallery.services.usuario.UsuarioService;
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
    public ResponseEntity<List<Usuario>> getUsuarios() {
        return ResponseEntity.ok(usuarioService.getUsuarios());
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody UsuarioRequest usuario_request) {
        Usuario result = usuarioService.createUsuario(usuario_request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + result.getId())).body(result);
    }

    @GetMapping("/{usuario_id}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable("usuario_id") Long usuario_id) {
        return ResponseEntity.ok(usuarioService.getUsuario(usuario_id));
    }

    @PatchMapping("/{usuario_id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable("usuario_id") Long usuario_id, @RequestBody UsuarioRequest usuario_request) {
        return ResponseEntity.ok(usuarioService.updateUsuario(usuario_id, usuario_request));
    }

    @GetMapping("/{usuario_id}/carrito")
    public ResponseEntity<Carrito> getCarrito(@PathVariable("usuario_id") Long usuario_id) {
        return ResponseEntity.ok(carritoService.getOrCreateCarritoByUsuario(usuario_id));
    }

    @GetMapping("/{usuario_id}/perfil-artista")
    public ResponseEntity<PerfilArtista> getPerfilArtista(@PathVariable("usuario_id") Long usuario_id) {
        return ResponseEntity.ok(perfilArtistaService.getPerfilArtistaByUsuario(usuario_id));
    }

    @PostMapping("/{usuario_id}/perfil-artista")
    public ResponseEntity<PerfilArtista> createPerfilArtista(
            @PathVariable("usuario_id") Long usuario_id,
            @RequestBody PerfilArtistaRequest perfil_artista_request) {
        PerfilArtista result = perfilArtistaService.createPerfilArtista(usuario_id, perfil_artista_request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuario_id + "/perfil-artista")).body(result);
    }

    @GetMapping("/{usuario_id}/compras")
    public ResponseEntity<List<Compra>> getCompras(@PathVariable("usuario_id") Long usuario_id) {
        return ResponseEntity.ok(compraService.getComprasByUsuario(usuario_id));
    }

    @PostMapping("/{usuario_id}/compras")
    public ResponseEntity<Compra> addCompra(@PathVariable("usuario_id") Long usuario_id) {
        return ResponseEntity.ok(compraService.createCompraForUsuario(usuario_id));
    }

    @GetMapping("/{usuario_id}/mensajes")
    public ResponseEntity<List<Mensaje>> getMensajes(@PathVariable("usuario_id") Long usuario_id) {
        return ResponseEntity.ok(mensajeService.getMensajesByUsuario(usuario_id));
    }
}
