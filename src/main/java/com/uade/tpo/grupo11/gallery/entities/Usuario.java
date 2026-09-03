package com.uade.tpo.grupo11.gallery.entities;

import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class Usuario {

    public Usuario() {}

    public Usuario (UsuarioRequest usuario_request) {
        this.nombre_usuario = usuario_request.getNombre_usuario();
        this.email_usuario = usuario_request.getEmail_usuario();
        this.contrasenia_usuario = usuario_request.getContrasenia_usuario();
    }

    public void patchFrom (UsuarioRequest usuario_request) {
        if (usuario_request.getNombre_usuario() != null){
            this.nombre_usuario = usuario_request.getNombre_usuario();
        }
        if (usuario_request.getEmail_usuario() != null){
            this.email_usuario = usuario_request.getEmail_usuario();
        }
        if (usuario_request.getContrasenia_usuario() != null){
            this.contrasenia_usuario = usuario_request.getContrasenia_usuario();
        }
        if (usuario_request.getNombre_persona() != null){
            this.nombre_usuario = usuario_request.getNombre_persona();
        }
        if (usuario_request.getAppelido_persona() != null){
            this.nombre_usuario = usuario_request.getAppelido_persona();
        }
        if (usuario_request.getTelefono_usuario() != null){
            this.telefono_usuario = usuario_request.getTelefono_usuario();
        }
    }

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
    private Date fecha_creacion;

    @Enumerated(EnumType.STRING)
    private Rol rol_usuario;

    @OneToOne(mappedBy = "usuario")
    private PerfilArtista perfil_artista;

    @OneToMany(mappedBy = "usuario_id")
    private List<Compra> compras_usuario;

    @OneToMany(mappedBy = "usuarioEmisor")
    private List<Mensaje> mensagens_usuario;

    @OneToOne(mappedBy = "usuario")
    private Carrito carrito_usuario;
}
