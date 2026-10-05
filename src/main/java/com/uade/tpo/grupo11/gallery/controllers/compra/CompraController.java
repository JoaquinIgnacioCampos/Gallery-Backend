package com.uade.tpo.grupo11.gallery.controllers.compra;

import com.uade.tpo.grupo11.gallery.controllers.paginacion.Paginacion;
import org.springframework.data.domain.Page;
import com.uade.tpo.grupo11.gallery.controllers.factura.FacturaResponse;
import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.services.compra.CompraService;
import com.uade.tpo.grupo11.gallery.services.factura.FacturaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Recibe las peticiones HTTP de las compras y devuelve la respuesta con su codigo. La logica vive en el service.
@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private FacturaService facturaService;


    // GET - Obtener todas las compras
    @GetMapping
    public ResponseEntity<Page<CompraResponse>> getCompras(
            @AuthenticationPrincipal Usuario usuarioActual,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(compraService.getCompras(usuarioActual, Paginacion.de(page, size))
                .map(CompraResponse::fromEntity));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<CompraResponse>> getMisCompras(
            @AuthenticationPrincipal Usuario usuarioActual,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(compraService.getMisCompras(usuarioActual, Paginacion.de(page, size))
                .map(CompraResponse::fromEntity));
    }

    // GET - Obtener una compra por ID
    // Este solo funciona para quen hizo la compra y para a quien le compraron y el ADMIN
    @GetMapping("/{compraId}")
    public ResponseEntity<CompraResponse> getCompraById(
            @PathVariable Long compraId,
            @AuthenticationPrincipal Usuario usuarioActual) {
        return ResponseEntity.ok(CompraResponse.fromEntity(compraService.getCompraById(compraId, usuarioActual)));
    }


    // POST - Crear compra
    @PostMapping
    public ResponseEntity<CompraResponse> createCompra(
            @Valid @RequestBody CompraRequest request) {

        Compra result = compraService.createCompra(request);
        return ResponseEntity.created(URI.create("/api/compras/" + result.getId()))
                .body(CompraResponse.fromEntity(result));
    }


    // PUT - Modificar compra
    @PutMapping("/{compraId}")
    public CompraResponse updateCompra(
            @PathVariable Long compraId,
            @Valid @RequestBody CompraRequest request) {

        return CompraResponse.fromEntity(compraService.updateCompra(
                compraId,
                request
        ));
    }


    // DELETE - Eliminar compra
    @DeleteMapping("/{compraId}")
    public void deleteCompra(
            @PathVariable Long compraId) {

        compraService.deleteCompra(compraId);
    }

/*      //Lo deje comentado porque la busqueda de las facturas por compra ya lo hace la clase Factura
        //Si quieren hacer la logica de la factura desde aca, hay que borrar lel metodo de factura y hacer lo de los permisos con esta clase
    // Devuelve las facturas de la compra.
    @GetMapping("/{compraId}/facturas")
    public ResponseEntity<List<FacturaResponse>> getFacturasByCompra(
            @PathVariable Long compraId,
            @AuthenticationPrincipal Usuario usuarioLogueado) {

        List<FacturaResponse> result = facturaService.getFacturasByCompra(compraId, usuarioLogueado).stream()
                .map(FacturaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }
*/
}
