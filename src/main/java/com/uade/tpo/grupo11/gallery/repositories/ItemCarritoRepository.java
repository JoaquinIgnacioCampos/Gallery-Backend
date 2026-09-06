package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    // Los items de un carrito: es lo que lee el checkout.
    List<ItemCarrito> findByCarritoId(Long carritoId);

    void deleteByCarritoId(Long carritoId);
}
