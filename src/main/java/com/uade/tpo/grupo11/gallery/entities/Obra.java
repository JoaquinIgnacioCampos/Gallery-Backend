package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(of = "id")               // identidad = id, evita recursión con relaciones
@ToString(exclude = {"variantes", "imagenes"})
@Entity
@Table(name = "obra")
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombreObra;              // camelCase en Java → columna nombre_obra

    @Column(length = 1000)
    private String descripcionObra;

    @Column(nullable = false)
    private boolean enVenta;

    // Muchas obras pertenecen a un artista. La FK vive en esta tabla.
    @ManyToOne
    @JoinColumn(name = "artista_id", nullable = false)
    private PerfilArtista artista;

    // Una obra tiene muchas variantes. La FK vive en Variante, en su campo "obra".
    @OneToMany(mappedBy = "obra")
    private List<Variante> variantes;

    @OneToMany(mappedBy = "obra")
    private List<Imagen> imagenes;
}
