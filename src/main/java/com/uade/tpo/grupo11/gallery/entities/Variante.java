package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"obra", "tamanioLienzo"})
@Entity
@Table(name = "variante")
public class Variante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variante_id")
    private Long id;

    // Muchas variantes pueden pertenecer a una misma obra
    @ManyToOne
    @JoinColumn(name = "obra_id", nullable = false)
    private Obra obra;

    // Muchas variantes pueden usar el mismo tamaño de lienzo
    @ManyToOne
    @JoinColumn(name = "id_tamanio", nullable = false)
    private TamanioLienzo tamanioLienzo;

    @Column(name = "precio_variante", nullable = false)
    private BigDecimal precioVariante;

    @Column(name = "stock_variante", nullable = false)
    private int stockVariante;

    // Integer y no int porque puede no haber descuento
    @Column(name = "porcentaje_descuento")
    private Integer porcentajeDescuento;

    // Puede ser null si no hay un descuento vigente
    @Column(name = "descuento_hasta")
    private LocalDate descuentoHasta;
}