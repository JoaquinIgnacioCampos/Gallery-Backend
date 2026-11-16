package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Carrito;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    // Busca por el id del usuario. Spring arma la consulta sola leyendo el nombre del metodo.
    Optional<Carrito> findByUsuarioId(Long usuarioId);

    // Lo mismo, pero bloquea la fila (SELECT ... FOR UPDATE) hasta que termine la transaccion. Lo usa el checkout.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Carrito c WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioIdParaActualizar(@Param("usuarioId") Long usuarioId);
}
