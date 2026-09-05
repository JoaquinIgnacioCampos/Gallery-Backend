package com.uade.tpo.grupo11.gallery.controllers.imagen;


import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.services.imagen.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Imagenes")
public class ImagenController {
    @Autowired
    private ImagenService servicioImagen;

    //Constructor
    public ImagenController(ImagenService servicioImagen) {
        this.servicioImagen = servicioImagen;
    }


    // GET - todas las imágenes
    @GetMapping
    public ResponseEntity<List<Imagen>> getImagenes() {
        return ResponseEntity.ok(servicioImagen.getImagenes());
    }

    // GET - una imagen por id
    @GetMapping("/{imagenId}")
    public ResponseEntity<Imagen> getImagenById(
            @PathVariable Long imagenId) {

        return ResponseEntity.ok(
                servicioImagen.getImagenById(imagenId)
        );
    }

    // POST - crear imagen
    @PostMapping
    public ResponseEntity<Imagen> createImagen(
            @RequestBody Imagen imagen) {

        return ResponseEntity.ok(
                servicioImagen.createImagen(imagen)
        );
    }

    // PUT - modificar imagen
    @PutMapping("/{imagenId}")
    public ResponseEntity<Imagen> updateImagen(
            @PathVariable Long imagenId,
            @RequestBody Imagen imagen) {

        return ResponseEntity.ok(
                servicioImagen.updateImagen(imagenId, imagen)
        );
    }

    // DELETE - eliminar imagen
    @DeleteMapping("/{imagenId}")
    public ResponseEntity<Void> deleteImagen(
            @PathVariable Long imagenId) {

        servicioImagen.deleteImagen(imagenId);

        return ResponseEntity.noContent().build();
    }
}
