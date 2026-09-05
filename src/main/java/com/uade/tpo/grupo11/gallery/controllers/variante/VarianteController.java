package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.services.variante.VarianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/Variantes")
public class VarianteController {

    @Autowired
    private VarianteService servicioVariante;

    //CONSTRUCTOR
    public VarianteController(VarianteService servicioVariante) {
        this.servicioVariante = servicioVariante;
    }

    @GetMapping()
    public ResponseEntity<List<Variante>> getVariantes() {
        return ResponseEntity.ok(servicioVariante.getVariantes());
    }


    @GetMapping("/varianteId")
    public ResponseEntity<Variante> getVarianteById(@PathVariable Long varianteId) {
        return ResponseEntity.ok(servicioVariante.getVarianteById(varianteId));
    }


    @PostMapping
    public ResponseEntity<Variante> createVariante(@RequestBody Variante variante) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(servicioVariante.createVariante(variante));
    }

    @PutMapping
    public ResponseEntity<Variante> updateVariante(
            @PathVariable Long varianteId,
            @RequestBody Variante variante) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(servicioVariante.updateVariante(varianteId, variante));


    }
    @DeleteMapping("/{varianteId}")
    public ResponseEntity<Void> deleteVariante(@PathVariable Long varianteId) {
    servicioVariante.deleteVariante(varianteId);
    return ResponseEntity.noContent().build();

    }




}
