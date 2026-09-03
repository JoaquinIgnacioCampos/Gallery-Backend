package com.uade.tpo.grupo11.gallery.controllers.usuario;

import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicatePerfilArtistaException;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUserMailException;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUsernameException;
import com.uade.tpo.grupo11.gallery.exceptions.PerfilArtistaNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.UsuarioNotFoundException;
import com.uade.tpo.grupo11.gallery.services.carrito.CarritoService;
import com.uade.tpo.grupo11.gallery.services.compra.CompraService;
import com.uade.tpo.grupo11.gallery.controllers.perfilartista.PerfilArtistaRequest;
import com.uade.tpo.grupo11.gallery.services.perfilartista.PerfilArtistaService;
import com.uade.tpo.grupo11.gallery.services.Mensaje.MensajeService;
import com.uade.tpo.grupo11.gallery.services.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
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
    public ResponseEntity<Usuario> createUsuario(@RequestBody UsuarioRequest usuario_request)
        throws DuplicateUsernameException, DuplicateUserMailException {
        Usuario result = usuarioService.createUsuario(usuario_request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + result.getUsuario_id())).body(result);
    }

    @GetMapping("/{usuario_id}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable("usuario_id") Long usuario_id) throws UsuarioNotFoundException {
        return ResponseEntity.ok(usuarioService.getUsuario(usuario_id));
    }

    @PatchMapping("/{usuario_id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable("usuario_id") Long usuario_id, @RequestBody UsuarioRequest usuario_request)
            throws DuplicateUsernameException, DuplicateUserMailException, UsuarioNotFoundException {
        return ResponseEntity.ok(usuarioService.updateUsuario(usuario_id, usuario_request));
    }

    @GetMapping("/{usuario_id}/carrito")
    public ResponseEntity<Carrito> getCarrito(@PathVariable("usuario_id") Long usuario_id) throws UsuarioNotFoundException {
        return ResponseEntity.ok(carritoService.getOrCreateCarritoByUsuario(usuario_id));
    }

    @GetMapping("/{usuario_id}/perfil-artista")
    public ResponseEntity<PerfilArtista> getPerfilArtista(@PathVariable("usuario_id") Integer usuarioId) {
    }

    @GetMapping("/{usuario_id}/compras")
    public ResponseEntity<List<Compra>> getCompras(@PathVariable("usuario_id") Long usuario_id) throws UsuarioNotFoundException {
        return ResponseEntity.ok(compraService.getComprasByUsuario(usuario_id));
    }

    @PostMapping("/{usuario_id}/compras")
    public ResponseEntity<List<Compra>> addCompra(@PathVariable("usuario_id") Long usuario_id) throws UsuarioNotFoundException {
        return ResponseEntity.ok(compraService.crearCompra(usuario_id));
    }

    @GetMapping("/{usuario_id}/mensajes")
    public ResponseEntity<List<Mensaje>> getMensajes(@PathVariable("usuario_id") Long usuario_id) throws UsuarioNotFoundException {
        return ResponseEntity.ok(mensajeService.obtenerPorUsuario(usuario_id));
    }


}
