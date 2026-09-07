package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import org.springframework.stereotype.Repository;
import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    // Busca por el id del artista. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Obra> findByArtistaId(Long artistaId);

    // Ambos limites deben cumplirse en la misma variante. DISTINCT evita obras repetidas.
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
