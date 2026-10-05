package com.uade.tpo.grupo11.gallery.controllers.paginacion;

import org.springframework.data.domain.PageRequest;

public final class Paginacion {

    private Paginacion() {
    }

    // Sin page y size se devuelve todo, igual que en la referencia del curso.
    public static PageRequest de(Integer page, Integer size) {
        if (page == null || size == null) {
            return PageRequest.of(0, Integer.MAX_VALUE);
        }
        return PageRequest.of(page, size);
    }
}
