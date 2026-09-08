package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    // Busca por el id del usuario. Spring arma la consulta sola leyendo el nombre del metodo.
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}
