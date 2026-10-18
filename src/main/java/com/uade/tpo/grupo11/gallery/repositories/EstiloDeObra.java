package com.uade.tpo.grupo11.gallery.repositories;

// Una fila de ObraRepository.findEstilosDeObras: un par (obra, estilo).
// Spring arma estos objetos solo, a partir de los alias (obraId, estiloId) de la consulta.
public interface EstiloDeObra {
    Long getObraId();
    Long getEstiloId();
}
