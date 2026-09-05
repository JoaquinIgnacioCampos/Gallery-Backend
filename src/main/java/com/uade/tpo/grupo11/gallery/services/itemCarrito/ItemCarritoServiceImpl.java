package com.uade.tpo.grupo11.gallery.services.itemcarrito;

import com.uade.tpo.grupo11.gallery.controllers.itemcarrito.ItemCarritoRequest;
import com.uade.tpo.grupo11.gallery.entities.Carrito;
import com.uade.tpo.grupo11.gallery.entities.ItemCarrito;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.CarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ItemCarritoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.MarcoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.CarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemCarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;

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
                .orElseThrow(() -> new ItemCarritoNotFoundException(itemId));
    }


    @Override
    public ItemCarrito createItemCarrito(ItemCarritoRequest request) {

        Carrito carrito = carritoRepository
                .findById(request.getCarrito_id())
                .orElseThrow(() -> new CarritoNotFoundException(request.getCarrito_id()));

        Marco marco = marcoRepository
                .findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Variante variante = varianteRepository
                .findById(request.getVariante_id())
                .orElseThrow(() -> new VarianteNotFoundException(request.getVariante_id()));

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
                .orElseThrow(() -> new ItemCarritoNotFoundException(itemId));

        Carrito carrito = carritoRepository
                .findById(request.getCarrito_id())
                .orElseThrow(() -> new CarritoNotFoundException(request.getCarrito_id()));

        Marco marco = marcoRepository
                .findById(request.getMarco_id())
                .orElseThrow(() -> new MarcoNotFoundException(request.getMarco_id()));

        Variante variante = varianteRepository
                .findById(request.getVariante_id())
                .orElseThrow(() -> new VarianteNotFoundException(request.getVariante_id()));

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
                .orElseThrow(() -> new ItemCarritoNotFoundException(itemId));

        itemCarritoRepository.delete(itemCarrito);
    }
}
