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

    // Las obras que no fueron dadas de baja. Spring arma la consulta sola leyendo el nombre del metodo.
    Page<Obra> findByEliminadaFalse(Pageable pageable);

    // Las obras vigentes de un artista: las eliminadas no se listan.
    List<Obra> findByArtistaIdAndEliminadaFalse(Long artistaId);
    Page<Obra> findByArtistaIdAndEliminadaFalse(Long artistaId, Pageable pageable);

    // Busqueda del catalogo con filtros OPCIONALES y combinables. El patron
    // ":param IS NULL OR condicion" hace que cada filtro se ignore si no vino.
    // Los dos limites de precio se evaluan sobre la MISMA variante (mismo LEFT JOIN),
    // y DISTINCT evita que una obra aparezca repetida por tener varias variantes o estilos.
    // Las obras eliminadas nunca entran en la busqueda.
    @Query(value = """
        SELECT DISTINCT o FROM Obra o
        LEFT JOIN o.variantes v
        LEFT JOIN o.estilos e
        WHERE o.eliminada = false
          AND (:artistaId IS NULL OR o.artista.id = :artistaId)
          AND (:estiloId IS NULL OR e.id = :estiloId)
          AND (:precioMin IS NULL OR v.precio_variante >= :precioMin)
          AND (:precioMax IS NULL OR v.precio_variante <= :precioMax)
        """,
           countQuery = """
        SELECT COUNT(DISTINCT o) FROM Obra o
        LEFT JOIN o.variantes v
        LEFT JOIN o.estilos e
        WHERE o.eliminada = false
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
