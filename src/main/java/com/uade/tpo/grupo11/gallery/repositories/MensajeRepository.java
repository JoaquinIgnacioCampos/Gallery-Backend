package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Busca por el id del encargo. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Mensaje> findByEncargoId(Long encargoId);

    // Busca por el id del emisor. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Mensaje> findByEmisorId(Long usuarioId);
}
