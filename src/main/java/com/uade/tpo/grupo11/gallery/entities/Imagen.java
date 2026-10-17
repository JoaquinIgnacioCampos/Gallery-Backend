package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

// ENTITY: representa la tabla "imagen". Es una tabla aparte y no una columna de Obra
// porque una obra tiene VARIAS fotos y con un orden; como columna solo entraria una.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "obra")
@Entity
@Table(name = "imagen")
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagen_id")
    private Long id;

    // Muchas imagenes pertenecen a una obra. La FK obra_id vive en esta tabla.
    @ManyToOne
    @JoinColumn(name = "obra_id", nullable = false)
    private Obra obra;

    // Posicion en la galeria: 1 es la principal, la que se ve en el catalogo.
    // Si el cliente no lo manda, el Service la agrega al final.
    @Column(name = "orden_imagen")
    private int orden_imagen;

    // El archivo vive en Cloudinary: en la base solo queda su direccion (https) y su public_id,
    // que es lo que hace falta para borrarlo de alla cuando se borra la imagen.
    @Column(name = "url_imagen", length = 500)
    private String url_imagen;

    @Column(name = "public_id_imagen")
    private String public_id_imagen;

}
