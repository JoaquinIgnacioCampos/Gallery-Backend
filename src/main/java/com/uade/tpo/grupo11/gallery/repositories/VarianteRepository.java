package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Variante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VarianteRepository extends JpaRepository<Variante, Long> {

    // Las variantes de una obra. Spring arma la consulta leyendo el nombre del metodo:
    // navega la relacion "obra" y filtra por su id.
    List<Variante> findByObraId(Long obraId);
}
