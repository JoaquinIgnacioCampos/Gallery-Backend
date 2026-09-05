package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilArtistaRepository extends JpaRepository<PerfilArtista, Long> {

    Optional<PerfilArtista> findByUsuarioId(Long usuarioId);

    @Override
    @EntityGraph(attributePaths = "usuario")
    List<PerfilArtista> findAll();

    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<PerfilArtista> findById(Long id);
}
