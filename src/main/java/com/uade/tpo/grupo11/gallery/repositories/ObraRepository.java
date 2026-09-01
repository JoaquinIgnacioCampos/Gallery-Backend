package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {
    // Los métodos CRUD vienen heredados. Acá van solo las consultas propias.
    List<Obra> findByArtistaId(Long artistaId);
    List<Obra> findByEnVentaTrue();
    List<Obra> findByNombreObraContainingIgnoreCase(String texto);
}
