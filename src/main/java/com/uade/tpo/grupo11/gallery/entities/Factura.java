package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_id")
    private Long facturaId;

    @ManyToOne
    @JoinColumn(name = "artista_id", nullable = false)
    private PerfilArtista artista;

    @OneToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @Column(name = "detalle_factura")
    private String detalleFactura;

    @Column(name = "precio_total_factura", nullable = false)
    private BigDecimal precioTotalFactura;

    @Column(name = "fecha_creacion_factura", nullable = false)
    private LocalDateTime fechaCreacionFactura;
}