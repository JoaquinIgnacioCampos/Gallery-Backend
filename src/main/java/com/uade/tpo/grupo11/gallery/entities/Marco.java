package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "marco")
public class Marco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marco")
    private Long idMarco;

    @Column(name = "nombre_marco", nullable = false)
    private String nombreMarco;

    @Column(name = "color_marco", nullable = false)
    private String colorMarco;

    @Lob
    @Column(name = "imagen_marco")
    private byte[] imagenMarco;

    @Column(name = "precio_marco", nullable = false)
    private BigDecimal precioMarco;
}
