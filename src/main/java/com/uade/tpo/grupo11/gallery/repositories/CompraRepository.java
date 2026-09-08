package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Busca por el id del usuario. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Compra> findByUsuarioId(Long usuarioId);
}
