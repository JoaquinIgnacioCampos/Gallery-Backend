package com.uade.tpo.grupo11.gallery.controllers.marco;

import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.services.marco.MarcoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de los marcos y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/marcos")
public class MarcoController {

    @Autowired
    private MarcoService marcoService;


    // GET - Obtener todos los marcos
    @GetMapping
    public List<Marco> getMarcos() {

        return marcoService.getMarcos();
    }


    // GET - Obtener un marco por ID
    @GetMapping("/{marcoId}")
    public Marco getMarcoById(
            @PathVariable Long marcoId) {

        return marcoService.getMarcoById(marcoId);
    }


    // POST - Crear marco
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Marco> createMarco(
            @Valid MarcoRequest request) throws IOException {

        Marco result = marcoService.createMarco(request);
        return ResponseEntity.created(URI.create("/api/marcos/" + result.getId())).body(result);
    }


    // PUT - Modificar marco
    @PutMapping(value = "/{marcoId}", consumes = "multipart/form-data")
    public Marco updateMarco(
            @PathVariable Long marcoId,
            @Valid MarcoRequest request) throws IOException {

        return marcoService.updateMarco(
                marcoId,
                request
        );
    }


    // DELETE - Eliminar marco
    @DeleteMapping("/{marcoId}")
    public void deleteMarco(
            @PathVariable Long marcoId) {

        marcoService.deleteMarco(marcoId);
    }
}