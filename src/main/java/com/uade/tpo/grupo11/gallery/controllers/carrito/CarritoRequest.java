package com.uade.tpo.grupo11.gallery.controllers.carrito;

import lombok.Data;

// Lo que el cliente manda para crear o modificar los carritos.
@Data
public class CarritoRequest {

    // El dueño NO se manda: es siempre el usuario logueado. Si viniera en el body,
    // cualquiera podria crear un carrito a nombre de otro.
    private String direccion_cliente;
}
