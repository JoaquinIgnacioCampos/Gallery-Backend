package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
// La respuesta de un encargo incluye los nombres del artista, el tamanio y el marco: el
// EntityGraph los trae en la misma consulta. Sin eso, cada encargo de una lista pediria esas relaciones
// (que son LAZY) de a una.
@Repository
public interface EncargoRepository extends JpaRepository<Encargo, Long> {

    @Override
    @EntityGraph(attributePaths = {"artista", "tamanio", "marco"})
    Optional<Encargo> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"artista", "tamanio", "marco"})
    Page<Encargo> findAll(Pageable pageable);

    // Busca por el id del artista. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Encargo> findByArtistaId(Long artistaId);

    @EntityGraph(attributePaths = {"artista", "tamanio", "marco"})
    Page<Encargo> findByArtistaId(Long artistaId, Pageable pageable);

    // Busca por el id del usuario. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Encargo> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"artista", "tamanio", "marco"})
    Page<Encargo> findByUsuarioId(Long usuarioId, Pageable pageable);
}
