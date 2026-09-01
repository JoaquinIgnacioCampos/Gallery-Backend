package com.uade.tpo.grupo11.gallery.controllers;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.services.ObraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.List;
import java.util.UUID;

// Atiende peticiones web y devuelve JSON (no vistas HTML)
@RestController
// Todos los endpoints de esta clase cuelgan de /obras
//mapea endpoints para que todas aparezcan en este path
@RequestMapping("/Obras")
public class ObraController {

    // Spring inyecta la implementación de ObraService; el controller solo conoce la interfaz
    @Autowired
    private ObraService servicioObra;
    //Otra forma es inyectarlo por contructor:

    //CONSTRUCTOR:
    //private final ObraService servicioObra;
   // public ObraController(ObraService servicioObra) {
      //  this.servicioObra = servicioObra;}

    @GetMapping()
    public ResponseEntity<List<Obra>> getListObras() {
        return ResponseEntity.ok(servicioObra.getListObras());
       //Leer de adentro hacia afuera:
        //1) Andá al servicio y pedile la lista de obras.
        //2) Agarrá eso que te dieron y preparalo como una respuesta HTTP 200 OK.
       //Estado: 200 OK
        // Contenido: [obra1, obra2, obra3]
        //Return --> Devolvé toda la respuesta al que hizo la petición

    }

    // GET /obras/{obraId} — el nombre del parámetro debe coincidir con el de la ruta
    @GetMapping("/{obraId}") //va entre llaves porque lo trasforma según el ID que es variable5
    public ResponseEntity<Obra> getObraById(@PathVariable Long obraId) {
        return ResponseEntity.ok(servicioObra.getObraById(obraId)); // 200 OK
    }// Cuando alguien hace GET /obras/{obraId},
// Busca y devuelve la obra correspondiente a ese ID.
// @PathVariable le indica a Spring que tome obraId
// desde la URL y lo guarde en esta variable.

// POST - Crear Nueva obra
@PostMapping
public ResponseEntity<Obra> crearObras(@RequestBody Obra obra) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(servicioObra.createObra(obra));
}

//PUT - Modifica valor de obra
@PutMapping("/{obraId}")
public ResponseEntity<Obra> actualizarObra(
        @PathVariable Long obraId,
        @RequestBody Obra obra){
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(servicioObra.modificarObra(obraId, obra));
//obraId --> Obra a Modificar
// obra --> Con qué nuevos datos quiero modificarla

}


//DELETE - void: no devuelve Obra
@DeleteMapping("/{obraId}")
public ResponseEntity<Void> eliminarObra(@PathVariable Long obraId){
    servicioObra.eliminarObra(obraId);
        return ResponseEntity.noContent().build();
        //noContent().build() devuelve 204 --> "salió bien, pero no te devuelvo contenido".






}}
