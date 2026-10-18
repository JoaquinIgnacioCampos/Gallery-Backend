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

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    // Regla única de "obra publicada": en venta, no eliminada, con al menos
    // una variante y al menos una imagen. Todas las consultas públicas la usan.
    String PUBLICADA = " o.eliminada = false AND o.en_venta = true "
            + "AND EXISTS (SELECT 1 FROM Variante vp WHERE vp.obra = o) "
            + "AND EXISTS (SELECT 1 FROM Imagen ip WHERE ip.obra = o)";

    @Query("SELECT o FROM Obra o WHERE " + PUBLICADA)
    Page<Obra> findDelCatalogo(Pageable pageable);

    @Query("SELECT o FROM Obra o WHERE o.artista.id = :artistaId AND " + PUBLICADA)
    List<Obra> findDelCatalogoByArtistaId(@Param("artistaId") Long artistaId);

    @Query("SELECT o FROM Obra o WHERE o.artista.id = :artistaId AND " + PUBLICADA)
    Page<Obra> findDelCatalogoByArtistaId(@Param("artistaId") Long artistaId, Pageable pageable);

    // Las obras que gestiona el propio artista: SIN la regla. Si no, no vería
    // la obra recién creada para cargarle las fotos y las variantes.
    Page<Obra> findByArtistaIdAndEliminadaFalse(Long artistaId, Pageable pageable);

    // Los ids de los estilos de varias obras en una sola consulta: una fila por cada par (obra, estilo).
    @Query("SELECT o.id AS obraId, e.id AS estiloId FROM Obra o JOIN o.estilos e WHERE o.id IN :obraIds")
    List<EstiloDeObra> findEstilosDeObras(@Param("obraIds") List<Long> obraIds);

    // Una fila de findEstilosDeObras.
    interface EstiloDeObra {
        Long getObraId();
        Long getEstiloId();
    }

    @Query(value = """
        SELECT DISTINCT o FROM Obra o
        JOIN o.variantes v
        LEFT JOIN o.estilos e
        WHERE """ + PUBLICADA + """
          AND (:artistaId IS NULL OR o.artista.id = :artistaId)
          AND (:estiloId IS NULL OR e.id = :estiloId)
          AND (:precioMin IS NULL OR v.precio_variante >= :precioMin)
          AND (:precioMax IS NULL OR v.precio_variante <= :precioMax)
        """,
            countQuery = """
        SELECT COUNT(DISTINCT o) FROM Obra o
        JOIN o.variantes v
        LEFT JOIN o.estilos e
        WHERE """ + PUBLICADA + """
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

    // Utilizado para vista de admincatalogo del front
    boolean existsByEstilosId(Long estiloId);
}
