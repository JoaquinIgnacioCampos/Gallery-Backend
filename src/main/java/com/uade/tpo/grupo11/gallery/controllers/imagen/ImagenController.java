package com.uade.tpo.grupo11.gallery.controllers.imagen;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.imagen.ImagenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

// CONTROLLER de Imagen. La diferencia con los otros dos es que aca entra un ARCHIVO,
// asi que los endpoints de alta y modificacion consumen multipart/form-data en lugar de JSON.
@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    // Inyeccion por constructor: el campo queda final y se puede testear sin Spring.
    private final ImagenService servicioImagen;

    public ImagenController(ImagenService servicioImagen) {
        this.servicioImagen = servicioImagen;
    }


    // GET /imagenes          -> todas
    // GET /imagenes?obraId=1 -> las de una obra, ordenadas
    @GetMapping
    public ResponseEntity<List<ImagenResponse>> getImagenes(
            @RequestParam(required = false) Long obraId) {

        List<ImagenResponse> result = (obraId != null
                ? servicioImagen.getImagenesByObra(obraId)
                : servicioImagen.getImagenes())
                .stream()
                .map(ImagenResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }


    @GetMapping("/{imagenId}")
    public ResponseEntity<ImagenResponse> getImagenById(@PathVariable Long imagenId) {
        return ResponseEntity.ok(ImagenResponse.fromEntity(servicioImagen.getImagenById(imagenId)));
    }


    // 201 CREATED, como en Obra y Variante.
    // consumes = multipart/form-data: el cuerpo no es JSON sino un formulario con el archivo
    // adjunto, que es la forma estandar de subir binarios en una API REST.
    // Por eso el Request va sin @RequestBody: Spring arma el objeto desde los campos del formulario.
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ImagenResponse> createImagen(
            @Valid ImagenRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) throws IOException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ImagenResponse.fromEntity(servicioImagen.createImagen(request, usuarioActual)));
    }


    @PutMapping(value = "/{imagenId}", consumes = "multipart/form-data")
    public ResponseEntity<ImagenResponse> updateImagen(
            @PathVariable Long imagenId,
            @Valid ImagenRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) throws IOException {

        return ResponseEntity.ok(ImagenResponse.fromEntity(servicioImagen.updateImagen(imagenId, request, usuarioActual)));
    }


    // DELETE devuelve 204 NO CONTENT: salio bien y no hay cuerpo que devolver.
    @DeleteMapping("/{imagenId}")
    public ResponseEntity<Void> deleteImagen(
            @PathVariable Long imagenId,
            @AuthenticationPrincipal Usuario usuarioActual) {
        servicioImagen.deleteImagen(imagenId, usuarioActual);
        return ResponseEntity.noContent().build();
    }
}
