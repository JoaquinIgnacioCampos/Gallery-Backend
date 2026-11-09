package com.uade.tpo.grupo11.gallery.controllers.adminmetricas;

import com.uade.tpo.grupo11.gallery.services.adminmetricas.AdminMetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// Endpoint exclusivo del administrador para consultar métricas generales.
@RestController
@RequestMapping("/api/admin/metricas")
public class AdminMetricasController {

    @Autowired
    private AdminMetricasService adminMetricasService;


    @GetMapping
    public ResponseEntity<AdminMetricasResponse> obtenerMetricas() {

        return ResponseEntity.ok(
                adminMetricasService.obtenerMetricas()
        );
    }
}
