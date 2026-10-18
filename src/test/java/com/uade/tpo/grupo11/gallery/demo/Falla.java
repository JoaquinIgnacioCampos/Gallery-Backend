package com.uade.tpo.grupo11.gallery.demo;

// Algo salio mal y no se puede seguir: el mensaje es para quien corre el script.
class Falla extends RuntimeException {
    Falla(String mensaje) {
        super(mensaje);
    }
}
