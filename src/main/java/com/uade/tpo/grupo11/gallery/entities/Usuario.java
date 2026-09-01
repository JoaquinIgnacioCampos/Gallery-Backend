package com.uade.tpo.grupo11.gallery.entities;

import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuario_id;

    @Column(unique = true, nullable = false)
    private String nombre_usuario;

    @Column(nullable = false)
    private String contrasenia_usuario;

    @Column
    private String nombre_persona;

    @Column
    private String appelido_persona;

    @Column
    private String email_usuario;

    @Column
    private String telefono_usuario;

    @Column
    private String direccion_usuario;

    @Enumerated(EnumType.STRING)
    private Rol rol_usuario;

    @OneToOne(mappedBy = "usuario_id")
    private Perfil_Artista perfil_artista;

    @OneToMany(mappedBy = "usuario_id")
    private List<Compra> compras_usuario;

    @OneToMany(mappedBy = "usuario_emisor")
    private List<Mensaje> mensagens_usuario;

    @OneToOne(mappedBy = "usuario_id")
    private Carrito carrito_usuario;
}
