package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Busca por el id del encargo. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Mensaje> findByEncargoId(Long encargoId);

    // Busca por el id del emisor. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Mensaje> findByEmisorId(Long usuarioId);

    // El nombre del campo en la entidad ya tiene guiones bajos (fecha_creacion_mensaje), asi que
    // un metodo derivado (findBy...OrderBy...) los interpreta como separadores de propiedad anidada
    // y falla. Se usa @Query explicita para evitar esa ambiguedad.
    @Query("SELECT m FROM Mensaje m WHERE m.encargo.id = :encargoId ORDER BY m.fecha_creacion_mensaje ASC")
    List<Mensaje> findByEncargoIdOrderByFecha_creacion_mensajeAsc(@Param("encargoId") Long encargoId);
}
