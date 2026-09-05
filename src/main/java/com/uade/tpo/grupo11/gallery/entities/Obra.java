package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")               // identidad = id, evita recursión con relaciones
@ToString(exclude = {"variantes", "imagenes", "estilos"})
@Entity
@Table(name = "obra")
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_obra", nullable = false, length = 150)
    private String nombre_obra;

    @Column(name = "descripcion_obra", length = 1000)
    private String descripcion_obra;

    @Column(name = "en_venta", nullable = false)
    private boolean en_venta;

    // Muchas obras pertenecen a un artista. La FK vive en esta tabla.
    @ManyToOne
    @JoinColumn(name = "artista_id", nullable = false)
    private PerfilArtista artista;

    // Una obra tiene muchas variantes. La FK vive en Variante, en su campo "obra".
    @OneToMany(mappedBy = "obra")
    private List<Variante> variantes;

    @OneToMany(mappedBy = "obra")
    private List<Imagen> imagenes;


    // La coneccion donde varias obras pueden tener varios estilos
    @ManyToMany
    @JoinTable(
            name = "estilo_obra",
            joinColumns = @JoinColumn(name = "obra_id"),
            inverseJoinColumns = @JoinColumn(name = "estilo_id")
    )
    private Set<Estilo> estilos = new HashSet<>();

}
