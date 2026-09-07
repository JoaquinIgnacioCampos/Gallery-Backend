package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// REPOSITORY de Imagen. Extiende JpaRepository, asi que el CRUD basico ya viene hecho.
@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {

    // Las imagenes de una obra, ordenadas por orden_imagen (1 = la principal).
    // La consulta va escrita a mano porque el campo se llama orden_imagen (con guion bajo)
    // y los query methods de Spring Data esperan camelCase para armar el ORDER BY.
    @Query("SELECT i FROM Imagen i WHERE i.obra.id = :obraId ORDER BY i.orden_imagen ASC")
    List<Imagen> findByObraIdOrdenadas(@Param("obraId") Long obraId);
}
