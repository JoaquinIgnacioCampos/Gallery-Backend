package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Estilo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface EstiloRepository extends JpaRepository<Estilo, Long> {
    // Busca un estilo por su nombre, para no cargar dos veces el mismo.
    Optional<Estilo> findByNombreEstilo(String nombreEstilo);
}