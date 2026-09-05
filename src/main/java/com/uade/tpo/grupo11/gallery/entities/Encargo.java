package com.uade.tpo.grupo11.gallery.entities;

import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "encargos")
@Data
public class Encargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "encargo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private PerfilArtista artista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tamanio_id", nullable = false)
    private TamanioLienzo tamanio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marco_id", nullable = false)
    private Marco marco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pintura", nullable = false)
    private TipoPintura tipo_pintura;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_lienzo", nullable = false)
    private TipoLienzo tipo_lienzo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_encargo", nullable = false)
    private EstadoEncargo estado_encargo;

    @Column(name = "descripcion_encargo")
    private String descripcion_encargo;

    @Column(name = "fecha_creacion_encargo", nullable = false, updatable = false)
    private LocalDateTime fecha_creacion_encargo;

    @PrePersist
    public void prePersist() {
        this.fecha_creacion_encargo = LocalDateTime.now();
    }
}
