package com.uade.tpo.grupo11.gallery.exceptions;


//Excepcion para la clase de Item_Factura
public class ItemFacturaNotFoundException extends RuntimeException {
    public ItemFacturaNotFoundException(Long id) {
        super("No se encontró el ítem de factura con el ID: " + id);
    }
}