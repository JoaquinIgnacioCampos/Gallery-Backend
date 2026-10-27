package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Busca por el id del usuario. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Compra> findByUsuarioId(Long usuarioId);
    Page<Compra> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Las compras de un usuario con filtros opcionales: rango de fechas (hasta es exclusivo) y como se entrego
    // (ver FiltroPorEntregaYFecha; una compra de un encargo no tiene tipo de entrega). Pageable SIN sort: ordena por id.
    @Query("SELECT compra FROM Compra compra WHERE compra.usuario.id = :usuarioId "
            + "AND compra.fecha_compra >= :desde AND compra.fecha_compra < :hasta "
            + "AND (:filtroDeEntrega = 0 OR (:filtroDeEntrega = 1 AND compra.tipo_entrega = :envioADomicilio) "
            + "OR (:filtroDeEntrega = 2 AND compra.tipo_entrega = :retiroEnElLocal) "
            + "OR (:filtroDeEntrega = 3 AND compra.tipo_entrega IS NULL)) "
            + "ORDER BY compra.id")
    Page<Compra> buscarDeUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("filtroDeEntrega") int filtroDeEntrega,
            @Param("envioADomicilio") TipoEntrega envioADomicilio,
            @Param("retiroEnElLocal") TipoEntrega retiroEnElLocal,
            Pageable pageable);
}
