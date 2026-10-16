package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import org.springframework.stereotype.Repository;
import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    // El catalogo publico: solo obras en venta y no eliminadas. Va con @Query porque el
    // guion bajo de "en_venta" Spring lo leeria como navegacion de una relacion.
    @Query("SELECT o FROM Obra o WHERE o.eliminada = false AND o.en_venta = true")
    Page<Obra> findDelCatalogo(Pageable pageable);

    // Las obras de un artista que ve el publico: en venta y no eliminadas.
    @Query("SELECT o FROM Obra o WHERE o.artista.id = :artistaId AND o.eliminada = false AND o.en_venta = true")
    List<Obra> findDelCatalogoByArtistaId(@Param("artistaId") Long artistaId);
    @Query("SELECT o FROM Obra o WHERE o.artista.id = :artistaId AND o.eliminada = false AND o.en_venta = true")
    Page<Obra> findDelCatalogoByArtistaId(@Param("artistaId") Long artistaId, Pageable pageable);

    // Las obras que gestiona el propio artista: tambien las que no estan en venta, pero no las eliminadas.
    Page<Obra> findByArtistaIdAndEliminadaFalse(Long artistaId, Pageable pageable);

    // Busqueda del catalogo con filtros OPCIONALES y combinables. El patron
    // ":param IS NULL OR condicion" hace que cada filtro se ignore si no vino.
    // Los dos limites de precio se evaluan sobre la MISMA variante (mismo LEFT JOIN),
    // y DISTINCT evita que una obra aparezca repetida por tener varias variantes o estilos.
    // Solo entran las obras en venta y no eliminadas.
    @Query(value = """
        SELECT DISTINCT o FROM Obra o
        JOIN o.variantes v
        LEFT JOIN o.estilos e
        WHERE o.eliminada = false
          AND o.en_venta = true
          AND (:artistaId IS NULL OR o.artista.id = :artistaId)
          AND (:estiloId IS NULL OR e.id = :estiloId)
          AND (:precioMin IS NULL OR v.precio_variante >= :precioMin)
          AND (:precioMax IS NULL OR v.precio_variante <= :precioMax)
        """,
           countQuery = """
        SELECT COUNT(DISTINCT o) FROM Obra o
        JOIN o.variantes v
        LEFT JOIN o.estilos e
        WHERE o.eliminada = false
          AND o.en_venta = true
          AND (:artistaId IS NULL OR o.artista.id = :artistaId)
          AND (:estiloId IS NULL OR e.id = :estiloId)
          AND (:precioMin IS NULL OR v.precio_variante >= :precioMin)
          AND (:precioMax IS NULL OR v.precio_variante <= :precioMax)
        """)
    Page<Obra> buscarConFiltros(
            @Param("artistaId") Long artistaId,
            @Param("estiloId") Long estiloId,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            Pageable pageable);
}
