package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PerfilArtista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre_artistico")
    private String nombre_artistico;

    @Column(name = "acepta_encargos", nullable = false)
    private boolean acepta_encargos;
}
