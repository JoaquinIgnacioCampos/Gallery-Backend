package com.uade.tpo.grupo11.gallery.repositories;


import com.uade.tpo.grupo11.gallery.entities.ItemFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemFacturaRepository extends JpaRepository<ItemFactura, Long> {
    List<ItemFactura> findByFacturaId(Long facturaId);
}
