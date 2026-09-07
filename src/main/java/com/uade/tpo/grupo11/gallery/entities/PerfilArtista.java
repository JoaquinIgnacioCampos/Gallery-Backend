package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data // Lombok genera getters, setters y otros métodos básicos de la clase.
@EqualsAndHashCode(of = "id") // Lombok genera equals y hashCode usando solamente el identificador.
@ToString(exclude = {"usuario", "obras", "estilos"}) // Excluye las relaciones de toString para evitar recursión al construir el texto.
@Entity // Indica a JPA que esta clase representa una entidad persistente.
@Table(name = "artista") // Vincula la entidad con la tabla artista de la base de datos.
public class PerfilArtista {

    @Id // Define este atributo como la clave primaria de la entidad.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Delega en la base de datos la generación incremental del ID.
    @Column(name = "artista_id") // Vincula el atributo con la columna artista_id definida en el DER.
    private Long id;


    @OneToOne(fetch = FetchType.LAZY, optional = false) // Cada artista corresponde a un único usuario y la relación es obligatoria.
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            unique = true
    ) // Configura usuario_id como una FK obligatoria y única dentro de la tabla artista.
    private Usuario usuario;

    @Column(name = "acepta_encargos", nullable = false) // Mapea el atributo a una columna obligatoria de la tabla.
    private boolean acepta_encargos;

    @Column(name = "nombre_artistico", nullable = false, length = 150) // Define el nombre de columna, su obligatoriedad y longitud máxima.
    private String nombre_artistico;

    @OneToMany(mappedBy = "artista") // Un artista puede tener muchas obras; la FK se administra desde Obra.artista.
    private List<Obra> obras = new ArrayList<>();

    // La coneccion donde un artista puede tener varios estilos
    @ManyToMany
    @JoinTable(
            name = "estilo_artista",
            joinColumns = @JoinColumn(name = "artista_id"),
            inverseJoinColumns = @JoinColumn(name = "estilo_id")
    )
    private Set<Estilo> estilos = new HashSet<>();
}
