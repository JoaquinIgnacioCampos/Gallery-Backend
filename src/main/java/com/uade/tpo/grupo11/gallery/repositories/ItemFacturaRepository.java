package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface ItemFacturaRepository extends JpaRepository<ItemFactura, Long> {

    // Busca por el id de la factura. Spring arma la consulta sola leyendo el nombre del metodo.
    List<ItemFactura> findByFacturaId(Long facturaId);


    // Cuantas obras (unidades) lleva cada factura, en una sola consulta.
    // Una factura sin items no aparece en el resultado.
    @Query("SELECT i.factura.id AS facturaId, SUM(i.cantidad_items) AS cantidad "
            + "FROM ItemFactura i WHERE i.factura.id IN :facturaIds GROUP BY i.factura.id")
    List<CantidadDeObras> contarObrasPorFacturas(@Param("facturaIds") List<Long> facturaIds);

    // Los items de una factura con su variante, obra, tamanio y marco ya cargados: una sola consulta
    // en vez de una por cada relacion de cada item.
    @Query("SELECT i FROM ItemFactura i JOIN FETCH i.variante v JOIN FETCH v.obra JOIN FETCH v.tamanio "
            + "JOIN FETCH i.marco WHERE i.factura.id = :facturaId ORDER BY i.id")
    List<ItemFactura> findDetalleByFacturaId(@Param("facturaId") Long facturaId);

    // Utilizado para vista de admincatalogo del front
    boolean existsByMarcoId(Long marcoId);
}
