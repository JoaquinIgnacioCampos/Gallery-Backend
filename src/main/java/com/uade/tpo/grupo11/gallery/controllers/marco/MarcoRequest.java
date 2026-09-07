package com.uade.tpo.grupo11.gallery.controllers.marco;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class MarcoRequest {

    private String nombre_marco;
    private String color_marco;
    private MultipartFile archivo;
    private BigDecimal precio_marco;
}
