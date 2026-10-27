package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Factura;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Busca por el id de la compra. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Factura> findByCompraId(Long compraId);
    Page<Factura> findByCompraId(Long compraId, Pageable pageable);

    // Las facturas emitidas a nombre de un artista: son sus ventas.
    List<Factura> findByArtistaId(Long artistaId);

    // Las ventas de un artista, de la mas nueva a la mas vieja. Con el id como desempate (varias facturas
    // pueden tener la misma fecha) para que las paginas no repitan ni omitan ventas.
    // Va escrita a mano porque los campos llevan guion bajo y el Sort de Spring los tomaria como rutas.
    // Devuelve solo las columnas que muestra la lista (no la entidad): asi no se cargan la compra ni quien
    // compro, que por ser relaciones EAGER se pedirian de a una por cada factura.
    // Filtros opcionales: rango de fechas (hasta es exclusivo) y como se entrego la compra (ver FiltroPorEntregaYFecha;
    // las ventas de encargos salen de una compra sin tipo de entrega). Las mismas condiciones filtran la lista y el total.
    String FILTROS_VENTAS = "WHERE factura.artista.id = :artistaId "
            + "AND factura.fecha_creacion_factura >= :desde AND factura.fecha_creacion_factura < :hasta "
            + "AND (:filtroDeEntrega = 0 OR (:filtroDeEntrega = 1 AND factura.compra.tipo_entrega = :envioADomicilio) "
            + "OR (:filtroDeEntrega = 2 AND factura.compra.tipo_entrega = :retiroEnElLocal) "
            + "OR (:filtroDeEntrega = 3 AND factura.compra.tipo_entrega IS NULL)) ";

    // Los "AS" de esta consulta no son alias de tablas: son los nombres de las columnas que lee VentaDeArtista.
    @Query(value = "SELECT factura.id AS id, factura.compra.id AS compraId, factura.fecha_creacion_factura AS fecha, "
            + "factura.precio_total_factura AS total FROM Factura factura " + FILTROS_VENTAS
            + "ORDER BY factura.fecha_creacion_factura DESC, factura.id DESC",
            countQuery = "SELECT COUNT(factura) FROM Factura factura " + FILTROS_VENTAS)
    Page<VentaDeArtista> findVentasDelArtista(
            @Param("artistaId") Long artistaId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("filtroDeEntrega") int filtroDeEntrega,
            @Param("envioADomicilio") TipoEntrega envioADomicilio,
            @Param("retiroEnElLocal") TipoEntrega retiroEnElLocal,
            Pageable pageable);

    // Lo vendido por un artista en total: la base suma todas sus facturas, no solo las de una pagina.
    // COALESCE porque el SUM de cero facturas es null y asi se devuelve 0.
    @Query("SELECT COALESCE(SUM(factura.precio_total_factura), 0) FROM Factura factura " + FILTROS_VENTAS)
    BigDecimal sumarVentasDelArtista(
            @Param("artistaId") Long artistaId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("filtroDeEntrega") int filtroDeEntrega,
            @Param("envioADomicilio") TipoEntrega envioADomicilio,
            @Param("retiroEnElLocal") TipoEntrega retiroEnElLocal);
}
