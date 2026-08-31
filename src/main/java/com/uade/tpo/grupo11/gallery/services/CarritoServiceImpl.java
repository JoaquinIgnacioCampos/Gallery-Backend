package com.uade.tpo.grupo11.gallery.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;

public class CarritoServiceImpl implements CarritoService{

    @Autowired
    private CarritoRepository repoCarrito;

    @Override
    public Carrito getCarritoById (Long carritoId){
        
        return repoCarrito.findById(carritoId)
                .orElseThrow(() -> new ObraNotFoundException(carritoId));
    }

}
