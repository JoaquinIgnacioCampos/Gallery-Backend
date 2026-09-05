package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tamanio_lienzo")
@Data
public class TamanioLienzo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tamanio")
    private Long id;

    @Column(name = "nombre_tamanio", nullable = false)
    private String nombre_tamanio;

    @Column(name = "ancho_lienzo", nullable = false)
    private Double ancho_lienzo;

    @Column(name = "largo_lienzo", nullable = false)
    private Double largo_lienzo;
}
