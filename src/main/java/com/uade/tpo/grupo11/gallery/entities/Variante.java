package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;

// ENTITY: representa la tabla "variante". Es LO QUE SE COMPRA: la obra en un tamanio
// concreto, con su propio precio y su propio stock. La misma obra en A4 y en A2 son
// dos variantes y una sola obra; por eso el precio y el stock estan aca y no en Obra.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"obra", "tamanio"})
@Entity
@Table(name = "variante")
public class Variante {

    public static final ZoneId ZONA = ZoneId.of("America/Argentina/Buenos_Aires");


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variante_id")
    private Long id;

    // Muchas variantes pertenecen a una obra. La FK obra_id vive en esta tabla.
    // nullable = false: una variante no puede existir sin su obra.
    @ManyToOne
    @JoinColumn(name = "obra_id", nullable = false)
    private Obra obra;

    // Muchas variantes pueden usar el mismo tamaño de lienzo
    @ManyToOne
    @JoinColumn(name = "id_tamanio", nullable = false)
    private TamanioLienzo tamanio;

    // BigDecimal y no double: los decimales de double pierden precision al operar,
    // y esto es plata. Es el precio de lista, sin descuento y sin marco.
    @Column(name = "precio_variante", nullable = false)
    private BigDecimal precio_variante;

    // Unidades disponibles de este tamanio. El checkout lo valida y lo descuenta.
    @Column(name = "stock_variante", nullable = false)
    private int stock_variante;

    // Integer y no int porque puede no haber descuento
    @Column(name = "porcentaje_descuento")
    private Integer porcentaje_descuento;

    // Puede ser null si no hay un descuento vigente
    @Column(name = "descuento_hasta")
    private LocalDate descuento_hasta;

    // Porcentaje que rige HOY. El descuento vale hasta el día indicado inclusive;
    public int getPorcentajeDescuentoVigente() {
        if (porcentaje_descuento == null || porcentaje_descuento <= 0) return 0;
        if (descuento_hasta != null && descuento_hasta.isBefore(LocalDate.now(ZONA))) return 0;
        return porcentaje_descuento;
    }

    // Precio de venta de la obra sola (sin marco), con el descuento vigente aplicado.
    public BigDecimal getPrecioConDescuento() {
        int pct = getPorcentajeDescuentoVigente();
        if (pct == 0) return precio_variante;
        return precio_variante
                .multiply(BigDecimal.valueOf(100 - pct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

}
