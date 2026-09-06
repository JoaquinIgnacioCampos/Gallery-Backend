package com.uade.tpo.grupo11.gallery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"obra", "tamanio"})
@Entity
@Table(name = "variante")
public class Variante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variante_id")
    private Long id;

    // Muchas variantes pueden pertenecer a una misma obra.
    // @JsonIgnore corta el ciclo Obra -> Variante -> Obra al armar el JSON.
    // Las variantes de una obra se consultan con GET /variantes?obraId=...
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "obra_id", nullable = false)
    private Obra obra;

    // Muchas variantes pueden usar el mismo tamaño de lienzo
    @ManyToOne
    @JoinColumn(name = "id_tamanio", nullable = false)
    private TamanioLienzo tamanio;

    @Column(name = "precio_variante", nullable = false)
    private BigDecimal precio_variante;

    @Column(name = "stock_variante", nullable = false)
    private int stock_variante;

    // Integer y no int porque puede no haber descuento
    @Column(name = "porcentaje_descuento")
    private Integer porcentaje_descuento;

    // Puede ser null si no hay un descuento vigente
    @Column(name = "descuento_hasta")
    private LocalDate descuento_hasta;
}
