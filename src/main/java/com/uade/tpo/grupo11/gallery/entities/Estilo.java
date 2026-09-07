package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "estilos")
@Data
public class Estilo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estilo_id")
    private Long id;

    @Column(name = "nombre_estilo", nullable = false, unique = true)
    private String nombreEstilo;

    @ManyToMany(mappedBy = "estilos")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PerfilArtista> artistas = new HashSet<>();

    @ManyToMany(mappedBy = "estilos")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Obra> obras = new HashSet<>();
}