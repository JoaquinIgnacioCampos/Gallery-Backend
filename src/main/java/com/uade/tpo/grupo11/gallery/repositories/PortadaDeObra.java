package com.uade.tpo.grupo11.gallery.repositories;

// Una fila de ImagenRepository.findPortadasDeObras: la obra y la URL de su portada.
// Spring arma estos objetos solo, a partir de los alias (obraId, url) de la consulta.
public interface PortadaDeObra {
    Long getObraId();
    String getUrl();
}
