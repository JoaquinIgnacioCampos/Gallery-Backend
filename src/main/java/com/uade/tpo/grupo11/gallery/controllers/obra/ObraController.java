package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.obra.ObraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.Parameter;

// CONTROLLER (capa de trafico): recibe la peticion HTTP y devuelve la respuesta con su
// codigo. No decide nada: toda la logica esta en el Service.
// @RestController = @Controller + @ResponseBody, o sea que cada metodo devuelve JSON y no una vista.
// @RequestMapping fija el prefijo comun de todas las rutas de esta clase.
@RestController
@RequestMapping("/api/obras")
public class ObraController {

    private final ObraService servicioObra;

    // Inyeccion por constructor: el campo queda final y se puede testear sin Spring.
    public ObraController(ObraService servicioObra) {
        this.servicioObra = servicioObra;
    }


    // GET /api/obras: filtros opcionales y combinables, con limites inclusivos.
    @GetMapping
    public ResponseEntity<List<ObraResponse>> getObras(
            @RequestParam(required = false) Long artistaId,
            @Parameter(description = "Categoria de la obra: ID del estilo. Opcional.")
            @RequestParam(required = false) Long estiloId,
            @Parameter(description = "Precio base minimo de una variante, inclusive; sin marco ni descuentos.")
            @RequestParam(required = false) BigDecimal precioMin,
            @Parameter(description = "Precio base maximo de la misma variante, inclusive; sin marco ni descuentos.")
            @RequestParam(required = false) BigDecimal precioMax) {

        List<ObraResponse> result = servicioObra.buscarConFiltros(artistaId, estiloId, precioMin, precioMax)
                .stream()
                .map(ObraResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }


    // El id viaja en la URL: es @PathVariable, no @RequestParam.
    // El nombre entre llaves y el del parametro tienen que coincidir letra por letra.
    // Si la obra no existe, el Service lanza la excepcion y el handler global responde 404.
    @GetMapping("/{obraId}")
    public ResponseEntity<ObraResponse> getObraById(@PathVariable Long obraId) {
        return ResponseEntity.ok(ObraResponse.fromEntity(servicioObra.getObraById(obraId)));
    }


    // POST crea un recurso nuevo, por eso devuelve 201 CREATED y no 200.
    // @Valid dispara las validaciones del Request ANTES de entrar al metodo:
    // si falta un campo obligatorio, nunca se llega al Service.
    @PostMapping
    public ResponseEntity<ObraResponse> createObra(
            @Valid @RequestBody ObraRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ObraResponse.fromEntity(servicioObra.createObra(request, usuarioLogueado)));
    }


    // PUT reemplaza la obra completa. El id va en la URL (@PathVariable) y los datos
    // en el cuerpo (@RequestBody): la URL dice CUAL y el body dice COMO queda.
    @PutMapping("/{obraId}")
    public ResponseEntity<ObraResponse> updateObra(
            @PathVariable Long obraId,
            @Valid @RequestBody ObraRequest request,
            @AuthenticationPrincipal Usuario usuarioLogueado) {

        return ResponseEntity.ok(ObraResponse.fromEntity(servicioObra.updateObra(obraId, request, usuarioLogueado)));
    }


    // DELETE devuelve 204 NO CONTENT: salio bien y no hay nada que devolver.
    @DeleteMapping("/{obraId}")
    public ResponseEntity<Void> deleteObra(
            @PathVariable Long obraId,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        servicioObra.deleteObra(obraId, usuarioLogueado);
        return ResponseEntity.noContent().build();
    }
}
