package com.uade.tpo.grupo11.gallery.controllers.marco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

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
