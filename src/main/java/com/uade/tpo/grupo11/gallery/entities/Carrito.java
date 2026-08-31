package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "id")               
@ToString(exclude = { "items_carrito"})
@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        
    

    @Column(length = 1000)
    private String direccion_cliente;

//     @OneToOne
//     @JoinColumn(name = "usuario_id", nullable = false)
//    private Usuario usuario;

}
