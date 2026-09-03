package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilArtistaRepository extends JpaRepository<PerfilArtista, Long> {

    @Query("select p from PerfilArtista p where p.usuario.usuario_id = ?1")
    Optional<PerfilArtista> findByUsuarioId(Long usuarioId);
}
