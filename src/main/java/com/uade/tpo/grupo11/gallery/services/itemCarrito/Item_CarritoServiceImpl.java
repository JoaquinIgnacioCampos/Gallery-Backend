package com.uade.tpo.grupo11.gallery.services.itemCarrito;

import com.uade.tpo.grupo11.gallery.controllers.Item_Carrito.Item_CarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.Item_Carrito;
// Agregar import com.uade.tpo.grupo11.gallery.entities.Marco;
// Agregar import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.Item_CarritoRepository;
// Agregar import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
// Agregar import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Item_CarritoServiceImpl implements Item_CarritoService {

    @Autowired
    private Item_CarritoRepository itemCarritoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private MarcoRepository marcoRepository;

    @Autowired
    private VarianteRepository varianteRepository;


    @Override
    public List<Item_Carrito> getItemsCarrito() {

        return itemCarritoRepository.findAll();
    }


    @Override
    public Item_Carrito getItemCarritoById(Long itemId) {

        return itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item del carrito no encontrado"));
    }


    @Override
    public Item_Carrito createItemCarrito(Item_CarritoRequest request) {

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

        Item_Carrito itemCarrito = ItemCarrito.builder()
                .carrito(carrito)
                .marco(marco)
                .variante(variante)
                .cantidad(request.getCantidad())
                .build();

        return itemCarritoRepository.save(itemCarrito);
    }


    @Override
    public Item_Carrito updateItemCarrito(
            Long itemId,
            Item_CarritoRequest request) {

        Item_Carrito itemCarrito = itemCarritoRepository
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

        Item_Carrito itemCarrito = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item del carrito no encontrado"));

        itemCarritoRepository.delete(itemCarrito);
    }
}