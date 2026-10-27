package com.uade.tpo.grupo11.gallery.services.filtro;

// El texto que se busca en una lista, listo para un LIKE.
public final class TextoDeBusqueda {

    private TextoDeBusqueda() {
    }

    // Sin espacios sobrantes, en minuscula, y con % _ ! escapados para que se busquen literalmente
    // (la consulta usa ESCAPE '!'). Un texto vacio no filtra: devuelve null.
    public static String patron(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String limpio = texto.trim().toLowerCase()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + limpio + "%";
    }
}
