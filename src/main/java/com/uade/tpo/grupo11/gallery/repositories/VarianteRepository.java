package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Variante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;

public interface VarianteRepository extends JpaRepository<Variante,Long> {
    // Consultas especiales → las agregamos nosotros en el repository.

}
