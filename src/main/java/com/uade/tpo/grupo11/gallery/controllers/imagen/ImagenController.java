package com.uade.tpo.grupo11.gallery.controllers.imagen;

import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.services.imagen.ImagenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService servicioImagen;

    public ImagenController(ImagenService servicioImagen) {
        this.servicioImagen = servicioImagen;
    }


    // GET /imagenes          -> todas
    // GET /imagenes?obraId=1 -> las de una obra, ordenadas
    @GetMapping
    public ResponseEntity<List<Imagen>> getImagenes(
            @RequestParam(required = false) Long obraId) {

        if (obraId != null) {
            return ResponseEntity.ok(servicioImagen.getImagenesByObra(obraId));
        }

        return ResponseEntity.ok(servicioImagen.getImagenes());
    }


    @GetMapping("/{imagenId}")
    public ResponseEntity<Imagen> getImagenById(@PathVariable Long imagenId) {
        return ResponseEntity.ok(servicioImagen.getImagenById(imagenId));
    }


    // 201 CREATED, como en Obra y Variante.
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Imagen> createImagen(@Valid ImagenRequest request) throws IOException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(servicioImagen.createImagen(request));
    }


    @PutMapping(value = "/{imagenId}", consumes = "multipart/form-data")
    public ResponseEntity<Imagen> updateImagen(
            @PathVariable Long imagenId,
            @Valid ImagenRequest request) throws IOException {

        return ResponseEntity.ok(servicioImagen.updateImagen(imagenId, request));
    }


    @DeleteMapping("/{imagenId}")
    public ResponseEntity<Void> deleteImagen(@PathVariable Long imagenId) {
        servicioImagen.deleteImagen(imagenId);
        return ResponseEntity.noContent().build();
    }
}
