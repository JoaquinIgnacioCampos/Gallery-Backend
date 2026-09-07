package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface EncargoRepository extends JpaRepository<Encargo, Long> {

    // Busca por el id del artista. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Encargo> findByArtistaId(Long artistaId);

    // Busca por el id del usuario. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Encargo> findByUsuarioId(Long usuarioId);
}
