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

// Recibe las peticiones HTTP de las imagenes y devuelve la respuesta con su codigo. La logica vive en el service.
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


    // Busca la imagen por id. Si no existe, se lanza la excepcion y el handler responde 404.
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


    // Actualiza la imagen: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @PutMapping(value = "/{imagenId}", consumes = "multipart/form-data")
    public ResponseEntity<ImagenResponse> updateImagen(
            @PathVariable Long imagenId,
            @Valid ImagenRequest request,
            @AuthenticationPrincipal Usuario usuarioActual) throws IOException {

        return ResponseEntity.ok(ImagenResponse.fromEntity(servicioImagen.updateImagen(imagenId, request, usuarioActual)));
    }


    // Elimina la imagen de la base.
    @DeleteMapping("/{imagenId}")
    public ResponseEntity<Void> deleteImagen(
            @PathVariable Long imagenId,
            @AuthenticationPrincipal Usuario usuarioActual) {
        servicioImagen.deleteImagen(imagenId, usuarioActual);
        return ResponseEntity.noContent().build();
    }
}
