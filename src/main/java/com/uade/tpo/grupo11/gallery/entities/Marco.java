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

    // La foto vive en Cloudinary (carpeta gallery/marcos): en la base solo queda su direccion
    // y su public_id, que hace falta para borrarla de alla.
    @Column(name = "url_marco", length = 500)
    private String url_marco;

    @Column(name = "public_id_marco")
    private String public_id_marco;

    @Column(name = "precio_marco", nullable = false)
    private BigDecimal precio_marco;
}
