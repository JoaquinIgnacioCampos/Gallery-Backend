package com.uade.tpo.grupo11.gallery.services.checkout;

import com.uade.tpo.grupo11.gallery.entities.Compra;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;

// Contrato del checkout: convertir el carrito de un usuario en una compra real.
public interface CheckoutService {

    Compra checkout(Long usuarioId, TipoEntrega tipoEntregaElegido);

}
