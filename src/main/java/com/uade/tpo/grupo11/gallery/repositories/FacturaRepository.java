package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Busca por el id de la compra. Spring arma la consulta sola leyendo el nombre del metodo.
    List<Factura> findByCompraId(Long compraId);
}
