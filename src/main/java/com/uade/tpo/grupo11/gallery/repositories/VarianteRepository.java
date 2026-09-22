package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Variante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface VarianteRepository extends JpaRepository<Variante, Long> {

    // Las variantes de una obra. Spring arma la consulta leyendo el nombre del metodo:
    // navega la relacion "obra" y filtra por su id.
    List<Variante> findByObraId(Long obraId);

    // Resta el stock de forma atomica: la condicion "stock >= cantidad" se evalua en la
    // misma sentencia SQL, asi que dos compras simultaneas de la ultima unidad no pueden
    // pisarse (una leeria el valor viejo si primero se hace un SELECT y despues un UPDATE
    // separado). Devuelve cuantas filas afecto: 0 significa que no habia stock suficiente
    // en ese instante.
    @Modifying
    @Query("UPDATE Variante v SET v.stock_variante = v.stock_variante - :cantidad " +
            "WHERE v.id = :id AND v.stock_variante >= :cantidad")
    int descontarStock(@Param("id") Long id, @Param("cantidad") int cantidad);
}
