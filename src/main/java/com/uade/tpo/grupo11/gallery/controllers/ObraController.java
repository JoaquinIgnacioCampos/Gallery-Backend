package com.uade.tpo.grupo11.gallery.controllers;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.services.ObraService;
import com.uade.tpo.grupo11.gallery.services.ObraServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


//Conecta con el Service
@Controller
//Qué endpoint va a ser - url
@RequestMapping("/obra")
public class ObraController {

    //conecta las capas, al controller cuando precise se le inyecta un Service
    @Autowired
    private ObraService servicioObra;

    @GetMapping("/{id}")
    public ResponseEntity<Obra> getObraById(@RequestParam Long obra_id){
    return ResponseEntity.ok(servicioObra.getObraById(obra_id)); // 200 - OK - respuesta http
    }

}
