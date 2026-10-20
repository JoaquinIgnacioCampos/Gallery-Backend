package com.uade.tpo.grupo11.gallery.repositories;

// Una fila de ImagenRepository.findPortadas y findPortadasByObraIds: la portada de una obra con solo las columnas
// que devuelve la API. Spring arma estos objetos solo, a partir de los alias de la consulta, sin cargar la obra
// (que por ser una relacion EAGER se pediria de a una por cada imagen).
public interface ImagenPortada {
    Long getId();
    Long getObraId();
    Integer getOrden();
    String getUrl();
}
