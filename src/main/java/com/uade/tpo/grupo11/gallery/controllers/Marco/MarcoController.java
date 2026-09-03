package com.uade.tpo.grupo11.gallery.controllers.Marco;

import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.services.Marco.MarcoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/marcos")
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
    @PostMapping
    public Marco createMarco(
            @RequestBody MarcoRequest request) {

        return marcoService.createMarco(request);
    }


    // PUT - Modificar marco
    @PutMapping("/{marcoId}")
    public Marco updateMarco(
            @PathVariable Long marcoId,
            @RequestBody MarcoRequest request) {

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