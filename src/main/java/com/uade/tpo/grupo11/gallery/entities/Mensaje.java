package com.uade.tpo.grupo11.gallery.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;


@Entity
@Table(name = "mensajes")
@Data

public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mensaje_id")
    private UUID id;

    // Relacion mensaje encargo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargo_id", nullable = false)
    private Encargo encargo;



    // Relacion usuario mensaje
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_emisor", nullable = false)
    private Usuario usuarioEmisor;

    @Column(name = "contenido_mensaje", nullable = false)
    private String contenidoMensaje;
}
