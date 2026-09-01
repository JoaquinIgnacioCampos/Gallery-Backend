package com.uade.tpo.grupo11.gallery.controllers.variante;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.services.variante.VarianteService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponseDecorator;
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
    public ResponseEntity<List<Variante>> getListVariante() {
        return ResponseEntity.ok(servicioVariante.getListVariante());
    }


    @GetMapping("/varianteId")
    public ResponseEntity<Variante> getVarianteById(@PathVariable Long varianteId) {
        return ResponseEntity.ok(servicioVariante.getVarianteById(varianteId));
    }


    @PostMapping
    public ResponseEntity<Variante> crearVariante(@RequestBody Variante variante) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(servicioVariante.createVariante(variante));
    }

    @PutMapping
    public ResponseEntity<Variante> actualizarVariante(
            @PathVariable Long varianteId,
            @RequestBody Variante variante) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(servicioVariante.modificarVariante(varianteId, variante));


    }
    @DeleteMapping("/{varianteId}")
    public ResponseEntity<Void> eliminarVariante(@PathVariable Long varianteId) {
    servicioVariante.eliminarVariante(varianteId);
    return ResponseEntity.noContent().build();

    }




}
