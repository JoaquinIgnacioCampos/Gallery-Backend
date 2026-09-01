package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.controllers.ItemCarrito.ItemCarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
// Agregar import com.uade.tpo.grupo11.gallery.entities.Marco;
// Agregar import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemCarritoRepository;
// Agregar import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
// Agregar import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemCarritoServiceImpl implements ItemCarritoService {

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private MarcoRepository marcoRepository;

    @Autowired
    private VarianteRepository varianteRepository;


    @Override
    public List<ItemCarrito> getItemsCarrito() {

        return itemCarritoRepository.findAll();
    }


    @Override
    public ItemCarrito getItemCarritoById(Long itemId) {

        return itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item del carrito no encontrado"));
    }


    @Override
    public ItemCarrito createItemCarrito(ItemCarritoRequest request) {

        Carrito carrito = carritoRepository
                .findById(request.getCarritoId())
                .orElseThrow(() ->
                        new RuntimeException("Carrito no encontrado"));

        Marco marco = marcoRepository
                .findById(request.getMarcoId())
                .orElseThrow(() ->
                        new RuntimeException("Marco no encontrado"));

        Variante variante = varianteRepository
                .findById(request.getVarianteId())
                .orElseThrow(() ->
                        new RuntimeException("Variante no encontrada"));

        ItemCarrito itemCarrito = ItemCarrito.builder()
                .carrito(carrito)
                .marco(marco)
                .variante(variante)
                .cantidad(request.getCantidad())
                .build();

        return itemCarritoRepository.save(itemCarrito);
    }


    @Override
    public ItemCarrito updateItemCarrito(
            Long itemId,
            ItemCarritoRequest request) {

        ItemCarrito itemCarrito = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item del carrito no encontrado"));

        Carrito carrito = carritoRepository
                .findById(request.getCarritoId())
                .orElseThrow(() ->
                        new RuntimeException("Carrito no encontrado"));

        Marco marco = marcoRepository
                .findById(request.getMarcoId())
                .orElseThrow(() ->
                        new RuntimeException("Marco no encontrado"));

        Variante variante = varianteRepository
                .findById(request.getVarianteId())
                .orElseThrow(() ->
                        new RuntimeException("Variante no encontrada"));

        itemCarrito.setCarrito(carrito);
        itemCarrito.setMarco(marco);
        itemCarrito.setVariante(variante);
        itemCarrito.setCantidad(request.getCantidad());

        return itemCarritoRepository.save(itemCarrito);
    }


    @Override
    public void deleteItemCarrito(Long itemId) {

        ItemCarrito itemCarrito = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item del carrito no encontrado"));

        itemCarritoRepository.delete(itemCarrito);
    }
}