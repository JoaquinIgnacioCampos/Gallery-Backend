package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    // Los items de un carrito: es lo que lee el checkout.
    List<ItemCarrito> findByCarritoId(Long carritoId);

    // Elimina el item del carrito de la base.
    void deleteByCarritoId(Long carritoId);
}
