package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.TamanioLienzo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface TamanioLienzoRepository extends JpaRepository<TamanioLienzo, Long> {
}
