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
    private Long id;

    @Column(name = "nombre_marco", nullable = false)
    private String nombre_marco;

    @Column(name = "color_marco", nullable = false)
    private String color_marco;

    @Lob
    @Column(name = "imagen_marco")
    private byte[] imagen_marco;

    @Column(name = "precio_marco", nullable = false)
    private BigDecimal precio_marco;
}
