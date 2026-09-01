package com.uade.tpo.grupo11.gallery.controllers.usuario;

import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUserMailException;
import com.uade.tpo.grupo11.gallery.exceptions.DuplicateUsernameException;
import com.uade.tpo.grupo11.gallery.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getUsuarios() {
        return ResponseEntity.ok(usuarioService.getUsuarios());
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody UsuarioRequest usuario_request)
        throws DuplicateUsernameException, DuplicateUserMailException {
        Usuario result =
    }

    @GetMapping("/{usuario_id}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable("usuario_id") Long usuario_id) {

    }

    @PatchMapping("/{usuario_id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable("usuario_id") Long usuario_id, @RequestBody Usuario usuario) {

    }


    @PostMapping("/{usuario_id}/crear-carrito")
    public ResponseEntity<Usuario> crearCarrito(@PathVariable("usuario_id") Long usuario_id) {

    }

    @GetMapping("/{usuario_id}/carrito")
    public ResponseEntity<Carrito> getCarrito(@PathVariable("usuarioId") Long usuarioId) {

    }

    @GetMapping("/{usuario_id}/perfil-artista")
    public ResponseEntity<Perfil_Artista> getPerfilArtista(@PathVariable("usuario_id") Long usuarioId) {

    }

    @GetMapping("/{usuario_id}/compras")
    public ResponseEntity<List<Compra>> getCompra(@PathVariable("usuarioId") Long usuarioId) {

    }

    @GetMapping("/{usuario_id}/mensajes")
    public ResponseEntity<List<Mensaje>> getMensajes(@PathVariable("usuarioId") Long usuarioId) {

    }


}
