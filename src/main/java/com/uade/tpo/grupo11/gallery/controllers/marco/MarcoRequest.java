package com.uade.tpo.grupo11.gallery.controllers.marco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

// Lo que el cliente manda para crear o modificar los marcos. Las relaciones viajan como ids.
@Data
public class MarcoRequest {

    @NotBlank(message = "El nombre del marco es obligatorio")
    private String nombre_marco;

    @NotBlank(message = "El color del marco es obligatorio")
    private String color_marco;

    private MultipartFile archivo;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio_marco;
}
