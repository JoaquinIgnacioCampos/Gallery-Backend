package com.uade.tpo.grupo11.gallery.controllers.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UsuarioRequest {
    private Long usuario_id;
    private String nombre_usuario;
    private String contrasenia_usuario;
    private String nombre_persona;
    private String apellido_persona;

    @Email(message = "El email no tiene un formato valido")
    private String email_usuario;

    private String telefono_usuario;
}
