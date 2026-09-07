package com.uade.tpo.grupo11.gallery.controllers.imagen;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// Lo que el cliente manda para subir o modificar una imagen.
// No es JSON: llega como formulario multipart, por eso el archivo es un MultipartFile.
@Data
public class ImagenRequest {

    @NotNull(message = "La obra es obligatoria")
    private Long obra_id;

    // Opcional: si no viene, el Service la agrega al final de la galeria.
    private Integer orden_imagen;

    // El archivo en si. MultipartFile es la clase de Spring para archivos subidos;
    // el Service lo pasa a byte[] con getBytes() para guardarlo en la columna BLOB.
    private MultipartFile archivo;
}
