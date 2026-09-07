package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {

    // Las imagenes de una obra, ordenadas por orden_imagen (1 = la principal).
    // Va escrita a mano porque el campo lleva guion bajo y los query methods esperan camelCase.
    @Query("SELECT i FROM Imagen i WHERE i.obra.id = :obraId ORDER BY i.orden_imagen ASC")
    List<Imagen> findByObraIdOrdenadas(@Param("obraId") Long obraId);
}
