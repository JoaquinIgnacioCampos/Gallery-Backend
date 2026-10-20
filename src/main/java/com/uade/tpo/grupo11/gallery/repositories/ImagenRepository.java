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

    // La portada (la imagen de menor orden) de cada obra. Una obra sin imagenes no aparece en el resultado.
    @Query("SELECT i.id AS id, i.obra.id AS obraId, i.orden_imagen AS orden, i.url_imagen AS url FROM Imagen i "
            + "WHERE i.orden_imagen = (SELECT MIN(i2.orden_imagen) FROM Imagen i2 WHERE i2.obra = i.obra) "
            + "ORDER BY i.obra.id ASC")
    List<ImagenPortada> findPortadas();

    // Lo mismo, pero solo de las obras pedidas (en una sola consulta con IN).
    @Query("SELECT i.id AS id, i.obra.id AS obraId, i.orden_imagen AS orden, i.url_imagen AS url FROM Imagen i "
            + "WHERE i.obra.id IN :obraIds "
            + "AND i.orden_imagen = (SELECT MIN(i2.orden_imagen) FROM Imagen i2 WHERE i2.obra = i.obra) "
            + "ORDER BY i.obra.id ASC")
    List<ImagenPortada> findPortadasByObraIds(@Param("obraIds") List<Long> obraIds);

    // La URL de la portada (la imagen de menor orden) de varias obras en una sola consulta.
    // Una obra sin imagenes no aparece en el resultado.
    @Query("SELECT i.obra.id AS obraId, i.url_imagen AS url FROM Imagen i "
            + "WHERE i.obra.id IN :obraIds "
            + "AND i.orden_imagen = (SELECT MIN(i2.orden_imagen) FROM Imagen i2 WHERE i2.obra = i.obra)")
    List<PortadaDeObra> findPortadasDeObras(@Param("obraIds") List<Long> obraIds);
}
