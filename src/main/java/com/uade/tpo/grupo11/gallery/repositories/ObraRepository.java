package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    // Regla única de "obra publicada": en venta, no eliminada, con al menos
    // una variante y al menos una imagen. Todas las consultas públicas la usan.
    // OJO: empieza con un espacio a propósito (los text blocks recortan los espacios del final).
    String PUBLICADA = " o.eliminada = false AND o.en_venta = true "
            + "AND EXISTS (SELECT 1 FROM Variante vp WHERE vp.obra = o) "
            + "AND EXISTS (SELECT 1 FROM Imagen ip WHERE ip.obra = o)";

    // Precio de una variante hoy: con su descuento solo si todavía no venció.
    // Se usa únicamente para ordenar; los límites precioMin/precioMax siguen siendo sobre el precio base.
    String PRECIO_VIGENTE = " CASE WHEN v.descuento_hasta IS NULL OR v.descuento_hasta >= :hoy "
            + "THEN v.precio_variante * (1 - COALESCE(v.porcentaje_descuento, 0) / 100.0) "
            + "ELSE v.precio_variante END ";

    // Filtros comunes de la búsqueda (el texto busca en nombre de obra y nombre artístico).
    // Estilos: con varios elegidos alcanza con que la obra tenga ALGUNO. "cantidadEstilos" evita
    // depender de una lista vacía (IN () no es válido): sin estilos se manda List.of(-1L) y no se usa.
    String FILTROS = " AND (:artistaId IS NULL OR o.artista.id = :artistaId) "
            + "AND (:cantidadEstilos = 0 OR e.id IN :estiloIds) "
            + "AND (:precioMin IS NULL OR v.precio_variante >= :precioMin) "
            + "AND (:precioMax IS NULL OR v.precio_variante <= :precioMax) "
            + "AND (:patron IS NULL OR LOWER(o.nombre_obra) LIKE :patron ESCAPE '!' "
            + "     OR LOWER(o.artista.nombre_artistico) LIKE :patron ESCAPE '!') ";

    @Query("SELECT o FROM Obra o WHERE " + PUBLICADA)
    Page<Obra> findDelCatalogo(Pageable pageable);

    @Query("SELECT o FROM Obra o WHERE o.artista.id = :artistaId AND " + PUBLICADA)
    List<Obra> findDelCatalogoByArtistaId(@Param("artistaId") Long artistaId);

    @Query("SELECT o FROM Obra o WHERE o.artista.id = :artistaId AND " + PUBLICADA)
    Page<Obra> findDelCatalogoByArtistaId(@Param("artistaId") Long artistaId, Pageable pageable);

    // Las obras que gestiona el propio artista: SIN la regla. Si no, no vería
    // la obra recién creada para cargarle las fotos y las variantes.
    Page<Obra> findByArtistaIdAndEliminadaFalse(Long artistaId, Pageable pageable);

    // Filtros opcionales y combinables de "Mis obras" (todos son AND; sin ninguno trae todas las del artista):
    //   estadoDeVenta                 : 0 todas, 1 solo las que estan en venta, 2 solo las que no
    //   estilos                       : con varios alcanza con que la obra tenga ALGUNO ("cantidadEstilos" evita una lista vacia)
    //   patronDeNombre                : texto en el nombre de la obra (ya escapado, ver TextoDeBusqueda)
    //   soloConVariantesSinStock      : solo las que tienen alguna variante sin stock
    String FILTROS_PROPIAS = "WHERE obra.artista.id = :artistaId AND obra.eliminada = false "
            + "AND (:estadoDeVenta = 0 OR (:estadoDeVenta = 1 AND obra.en_venta = true) "
            + "OR (:estadoDeVenta = 2 AND obra.en_venta = false)) "
            + "AND (:cantidadEstilos = 0 OR estilo.id IN :estiloIds) "
            + "AND (:patronDeNombre IS NULL OR LOWER(obra.nombre_obra) LIKE :patronDeNombre ESCAPE '!') "
            + "AND (:soloConVariantesSinStock = false OR EXISTS (SELECT 1 FROM Variante varianteSinStock "
            + "WHERE varianteSinStock.obra = obra AND varianteSinStock.stock_variante = 0)) ";

    // Pageable debe venir SIN sort: el orden (por id) lo define la propia consulta, asi las paginas no repiten ni omiten obras.
    @Query(value = "SELECT DISTINCT obra FROM Obra obra LEFT JOIN obra.estilos estilo " + FILTROS_PROPIAS + "ORDER BY obra.id",
            countQuery = "SELECT COUNT(DISTINCT obra) FROM Obra obra LEFT JOIN obra.estilos estilo " + FILTROS_PROPIAS)
    Page<Obra> buscarPropias(
            @Param("artistaId") Long artistaId,
            @Param("estadoDeVenta") int estadoDeVenta,
            @Param("cantidadEstilos") int cantidadEstilos,
            @Param("estiloIds") List<Long> estiloIds,
            @Param("patronDeNombre") String patronDeNombre,
            @Param("soloConVariantesSinStock") boolean soloConVariantesSinStock,
            Pageable pageable);

    // Los ids de los estilos de varias obras en una sola consulta: una fila por cada par (obra, estilo).
    @Query("SELECT o.id AS obraId, e.id AS estiloId FROM Obra o JOIN o.estilos e WHERE o.id IN :obraIds")
    List<EstiloDeObra> findEstilosDeObras(@Param("obraIds") List<Long> obraIds);

    // Búsqueda del catálogo. El orden se decide con "signo":
    //   0  -> más recientes (id descendente)
    //   1  -> menor precio primero (precio más bajo de cada obra, con descuento vigente)
    //  -1  -> mayor precio primero
    // Se agrupa por obra para ordenar por su variante más barata sin duplicar filas.
    // Pageable debe venir SIN sort: el orden lo define la propia consulta.
    @Query(value = "SELECT o FROM Obra o JOIN o.variantes v LEFT JOIN o.estilos e "
            + "WHERE " + PUBLICADA + FILTROS
            + "GROUP BY o "
            + "ORDER BY :signo * MIN(" + PRECIO_VIGENTE + ") ASC, o.id DESC",
            countQuery = "SELECT COUNT(DISTINCT o) FROM Obra o JOIN o.variantes v LEFT JOIN o.estilos e "
                    + "WHERE " + PUBLICADA + FILTROS)
    Page<Obra> buscarConFiltros(
            @Param("artistaId") Long artistaId,
            @Param("cantidadEstilos") int cantidadEstilos,
            @Param("estiloIds") List<Long> estiloIds,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("patron") String patron,
            @Param("hoy") LocalDate hoy,
            @Param("signo") int signo,
            Pageable pageable);

    // Utilizado para vista de admincatalogo del front
    boolean existsByEstilosId(Long estiloId);
}
