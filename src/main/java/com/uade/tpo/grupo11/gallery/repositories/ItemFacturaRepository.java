package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface ItemFacturaRepository extends JpaRepository<ItemFactura, Long> {

    // Busca por el id de la factura. Spring arma la consulta sola leyendo el nombre del metodo.
    List<ItemFactura> findByFacturaId(Long facturaId);
}
