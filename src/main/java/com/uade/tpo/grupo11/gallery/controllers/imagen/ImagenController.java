package com.uade.tpo.grupo11.gallery.controllers.imagen;

import com.uade.tpo.grupo11.gallery.entities.Imagen;
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


    // GET /imagenes                                  -> todas
    // GET /imagenes?obraId=1                         -> las de una obra, ordenadas
    // GET /imagenes?portada=true                     -> solo la portada (la principal) de cada obra
    // GET /imagenes?portada=true&obraId=1&obraId=2   -> la portada de esas obras, en un solo pedido
    @GetMapping
    public ResponseEntity<List<ImagenResponse>> getImagenes(
            @RequestParam(required = false) List<Long> obraId,
            @RequestParam(defaultValue = "false") boolean portada) {

        boolean variasObras = obraId != null && obraId.size() > 1;
        if (variasObras && !portada) {
            throw new IllegalArgumentException(
                    "Para pedir las imágenes de varias obras a la vez hay que usar portada=true");
        }

        if (portada) {
            return ResponseEntity.ok(servicioImagen.getPortadas(obraId).stream()
                    .map(ImagenResponse::fromPortada)
                    .toList());
        }

        List<Imagen> imagenes;
        if (obraId != null && !obraId.isEmpty()) {
            imagenes = servicioImagen.getImagenesByObra(obraId.get(0));
        } else {
            imagenes = servicioImagen.getImagenes();
        }

        return ResponseEntity.ok(imagenes.stream().map(ImagenResponse::fromEntity).toList());
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
