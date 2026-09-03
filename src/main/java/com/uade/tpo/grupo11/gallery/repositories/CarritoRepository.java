package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    @Query("select c from Carrito c where c.usuario.usuario_id = ?1")
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}
