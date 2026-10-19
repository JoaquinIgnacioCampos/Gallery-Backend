package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.PerfilArtista;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Page<PerfilArtista> findAll(Pageable pageable);

    // Sobrescribimos el metodo de JpaRepository para traer tambien el usuario en la misma consulta.
    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<PerfilArtista> findById(Long id);

    // Utilizado en vista de AdminCatalogo del front
    boolean existsByEstilosId(Long estiloId);


    // Busqueda de artistas: por texto en el nombre artistico y por estilos. Los estilos se toman de las
    // obras PUBLICADAS del artista (la misma regla del catalogo): alcanza con que tenga obras de ALGUNO
    // de los estilos elegidos. Pageable debe venir SIN sort: el orden lo define la consulta.
    @Query(value = "SELECT a FROM PerfilArtista a WHERE "
            + "(:patron IS NULL OR LOWER(a.nombre_artistico) LIKE :patron ESCAPE '!') "
            + "AND (:cantidadEstilos = 0 OR EXISTS (SELECT 1 FROM Obra o JOIN o.estilos e "
            + "WHERE o.artista = a AND e.id IN :estiloIds AND " + ObraRepository.PUBLICADA + ")) "
            + "ORDER BY a.id ASC",
            countQuery = "SELECT COUNT(a) FROM PerfilArtista a WHERE "
                    + "(:patron IS NULL OR LOWER(a.nombre_artistico) LIKE :patron ESCAPE '!') "
                    + "AND (:cantidadEstilos = 0 OR EXISTS (SELECT 1 FROM Obra o JOIN o.estilos e "
                    + "WHERE o.artista = a AND e.id IN :estiloIds AND " + ObraRepository.PUBLICADA + "))")
    Page<PerfilArtista> buscar(@Param("patron") String patron,
                               @Param("cantidadEstilos") int cantidadEstilos,
                               @Param("estiloIds") List<Long> estiloIds,
                               Pageable pageable);
}
