package com.uade.tpo.grupo11.gallery.controllers.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UsuarioRequest {
    private Long usuario_id;
    private String nombre_usuario;
    private String contrasenia_usuario;
    private String nombre_persona;
    private String appelido_persona;
    private String email_usuario;
    private String telefono_usuario;
}
