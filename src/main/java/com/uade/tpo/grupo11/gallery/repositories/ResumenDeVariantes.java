package com.uade.tpo.grupo11.gallery.repositories;

// Una fila de VarianteRepository.resumirPorObras: cuantas variantes tiene una obra y cuantas estan sin stock.
// Spring arma estos objetos solo, a partir de los alias (obraId, cantidad, sinStock) de la consulta.
public interface ResumenDeVariantes {
    Long getObraId();
    Long getCantidad();
    Long getSinStock();
}
