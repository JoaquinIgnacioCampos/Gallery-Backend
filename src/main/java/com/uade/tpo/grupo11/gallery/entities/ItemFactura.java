package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "items_factura")
@Data
public class ItemFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_factura_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marco_id", nullable = false)
    private Marco marco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private Variante variante;

    @Column(name = "cantidad_items", nullable = false)
    private Integer cantidad_items;

    @Column(name = "total_item", nullable = false)
    private BigDecimal total_item;

    @Column(name = "descuento", nullable = false)
    private BigDecimal descuento = BigDecimal.ZERO;
}
