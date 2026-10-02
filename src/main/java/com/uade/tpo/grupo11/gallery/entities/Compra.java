package com.uade.tpo.grupo11.gallery.entities;

import com.uade.tpo.grupo11.gallery.entities.enums.TipoEntrega;
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
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fecha_compra;

    @Column(name = "total_compra", nullable = false)
    private BigDecimal total_compra;

    //Agregamos estos dos para que despues en la factura quede fijo lo que se seleccione
    // y no sea algo que dependa de otra clase que puede cambiar
    @Column(name = "direccion_entrega")
    private String direccion_entrega;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega")
    private TipoEntrega tipo_entrega;

    //Esto es para la constante del pecio por envio
    @Column(name = "costo_envio")
    private BigDecimal costo_envio;

}
