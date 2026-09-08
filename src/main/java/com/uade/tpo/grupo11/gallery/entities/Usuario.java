package com.uade.tpo.grupo11.gallery.entities;

import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Usuario implements UserDetails{

    public Usuario() {}

    public Usuario (UsuarioRequest usuario_request) {
        this.nombre_usuario = usuario_request.getNombre_usuario();
        this.email_usuario = usuario_request.getEmail_usuario();
        this.contrasenia_usuario = usuario_request.getContrasenia_usuario();
        this.nombre_persona = usuario_request.getNombre_persona();
        this.apellido_persona = usuario_request.getApellido_persona();
        this.telefono_usuario = usuario_request.getTelefono_usuario();
    }

    // Copia al usuario solo los campos que vinieron en el request. Los que llegan nulos no se pisan.
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
            this.nombre_persona = usuario_request.getNombre_persona();
        }
        if (usuario_request.getApellido_persona() != null){
            this.apellido_persona = usuario_request.getApellido_persona();
        }
        if (usuario_request.getTelefono_usuario() != null){
            this.telefono_usuario = usuario_request.getTelefono_usuario();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre_usuario;

    @Column(nullable = false)
    private String contrasenia_usuario;

    @Column
    private String nombre_persona;

    @Column
    private String apellido_persona;

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

    @OneToMany(mappedBy = "usuario")
    private List<Compra> compras_usuario;

    @OneToMany(mappedBy = "emisor")
    private List<Mensaje> mensajes_usuario;

    @OneToOne(mappedBy = "usuario")
    private Carrito carrito_usuario;

    // Se ejecuta justo antes del primer guardado: pone la fecha de creacion y el rol por defecto.
    @PrePersist
    private void onCreate() {
        if (this.fecha_creacion == null) {
            this.fecha_creacion = new Date();
        }
        if (this.rol_usuario == null) {
            this.rol_usuario = Rol.CLIENTE;
        }
    }

    // ---- Métodos de UserDetails ----

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol_usuario.name()));
    }

    // Metodo de UserDetails: le dice a Spring Security donde esta la contrasenia.
    @Override
    public String getPassword() {
        return contrasenia_usuario;
    }

    // Metodo de UserDetails: nuestro nombre de usuario para loguearse es el email.
    @Override
    public String getUsername() {
        return email_usuario; // el login se hace con el mail
    }

    // Metodo de UserDetails. No manejamos vencimiento de cuentas, asi que siempre true.
    @Override
    public boolean isAccountNonExpired() { return true; }

    // Metodo de UserDetails. No manejamos bloqueo de cuentas, asi que siempre true.
    @Override
    public boolean isAccountNonLocked() { return true; }

    // Metodo de UserDetails. No vencemos contrasenias, asi que siempre true.
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // Metodo de UserDetails. Todas las cuentas estan activas.
    @Override
    public boolean isEnabled() { return true; }

}
