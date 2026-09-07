package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @ManyToOne
    @JoinColumn(name = "obra_id", nullable = false)
    private Obra obra;

    @Column(name = "orden_imagen")
    private int orden_imagen;

    // El DER define contenido_imagen como BLOB
    @Lob
    @Column(name = "contenido_imagen")
    private byte[] contenido_imagen;
}
