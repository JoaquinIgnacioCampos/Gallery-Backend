package com.uade.tpo.grupo11.gallery.controllers.marco;

import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.services.marco.MarcoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public Marco createMarco(
            @Valid MarcoRequest request) throws IOException {

        return marcoService.createMarco(request);
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