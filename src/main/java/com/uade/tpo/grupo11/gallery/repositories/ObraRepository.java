package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import org.springframework.stereotype.Repository;
import java.util.List;

// REPOSITORY: la unica capa que habla con la base. Es una INTERFAZ: no se implementa,
// Spring Data genera la clase sola. Al extender JpaRepository ya trae findAll, findById,
// save, deleteById y existsById sin escribir una linea de SQL.
@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    // Query method: Spring lee el nombre y arma la consulta. findBy + Artista + Id
    // significa "navega la relacion artista y filtra por su id".
    List<Obra> findByArtistaId(Long artistaId);

    // Busqueda del catalogo con filtros OPCIONALES y combinables. El patron
    // ":param IS NULL OR condicion" hace que cada filtro se ignore si no vino.
    // Los dos limites de precio se evaluan sobre la MISMA variante (mismo LEFT JOIN),
    // y DISTINCT evita que una obra aparezca repetida por tener varias variantes o estilos.
    @Query("""
        SELECT DISTINCT o FROM Obra o
        LEFT JOIN o.variantes v
        LEFT JOIN o.estilos e
        WHERE (:artistaId IS NULL OR o.artista.id = :artistaId)
          AND (:estiloId IS NULL OR e.id = :estiloId)
          AND (:precioMin IS NULL OR v.precio_variante >= :precioMin)
          AND (:precioMax IS NULL OR v.precio_variante <= :precioMax)
        """)
    List<Obra> buscarConFiltros(
            @Param("artistaId") Long artistaId,
            @Param("estiloId") Long estiloId,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax);
}
