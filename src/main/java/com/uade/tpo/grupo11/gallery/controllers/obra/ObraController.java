package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.services.obra.ObraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/obras")
public class ObraController {

    @Autowired
    private ObraService servicioObra;


    //CONSTRUCTOR:
   public ObraController(ObraService servicioObra) {
       this.servicioObra = servicioObra;}


    @GetMapping()
    public ResponseEntity<List<Obra>> getListObras() {
        return ResponseEntity.ok(servicioObra.getListObras());
    }

    @GetMapping("/{obraId}")
    public ResponseEntity<Obra> getObraById(@PathVariable Long obraId) {
        return ResponseEntity.ok(servicioObra.getObraById(obraId));
    }

    @PostMapping
    public ResponseEntity<Obra> crearObra(@RequestBody Obra obra) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(servicioObra.createObra(obra));
    }

    @PutMapping("/{obraId}")
    public ResponseEntity<Obra> actualizarObra(
            @PathVariable Long obraId,
            @RequestBody Obra obra
    ) {
        return ResponseEntity.ok(servicioObra.modificarObra(obraId, obra));
    }

    @DeleteMapping("/{obraId}")
    public ResponseEntity<Void> eliminarObra(@PathVariable Long obraId) {
        servicioObra.eliminarObra(obraId);
        return ResponseEntity.noContent().build();
    }
}
