package com.uade.tpo.grupo11.gallery.controllers.paginacion;

import org.springframework.data.domain.PageRequest;

public final class Paginacion {

    // Tope de elementos por pagina: sin el, un size enorme pide toda la tabla en una sola consulta.
    public static final int TAMANIO_MAXIMO = 100;

    private Paginacion() {
    }

    // Sin page y size se devuelve todo, igual que en la referencia del curso.
    public static PageRequest de(Integer page, Integer size) {
        if (page == null || size == null) {
            return PageRequest.of(0, Integer.MAX_VALUE);
        }
        // Sin esto, PageRequest.of corta con su propio mensaje en ingles.
        if (page < 0) {
            throw new IllegalArgumentException("La pagina no puede ser negativa");
        }
        if (size < 1) {
            throw new IllegalArgumentException("El tamanio de pagina tiene que ser al menos 1");
        }
        return PageRequest.of(page, Math.min(size, TAMANIO_MAXIMO));
    }
}
