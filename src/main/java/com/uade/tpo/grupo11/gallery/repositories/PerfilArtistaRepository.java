package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface PerfilArtistaRepository extends JpaRepository<PerfilArtista, Long> {

    // Busca por el id del usuario. Spring arma la consulta sola leyendo el nombre del metodo.
    Optional<PerfilArtista> findByUsuarioId(Long usuarioId);

    // Sobrescribimos el metodo de JpaRepository para traer tambien el usuario en la misma consulta.
    @Override
    @EntityGraph(attributePaths = "usuario")
    List<PerfilArtista> findAll();

    // Sobrescribimos el metodo de JpaRepository para traer tambien el usuario en la misma consulta.
    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<PerfilArtista> findById(Long id);
}
