package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
@Data
@Entity
@Table
public class Obra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String nombre_obra;
    @Column
    private String descripcion_obra ;
    @Column
    private Boolean en_venta_obra ;
}
