package com.uade.tpo.grupo11.gallery.services.almacenamiento;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// Contrato: que sabe hacer el servicio que guarda los archivos de las imagenes. La implementacion
// es la que habla con el servicio externo (hoy Cloudinary).
public interface AlmacenamientoService {

    // Carpetas del servicio externo: cada tipo de imagen va a la suya.
    String CARPETA_OBRAS = "gallery/obras";
    String CARPETA_MARCOS = "gallery/marcos";

    // Lo que devuelve una subida: lo que hay que guardar en la base.
    record ImagenSubida(String url, String publicId) {
    }

    // Verifica que el archivo sea una imagen mirando su contenido. Si no lo es, se lanza
    // IllegalArgumentException y el handler responde 400.
    void validarImagen(MultipartFile archivo) throws IOException;

    // Sube el archivo a la carpeta indicada y devuelve su URL (https) y su public_id.
    ImagenSubida subir(MultipartFile archivo, String carpeta) throws IOException;

    // Borra el archivo ya mismo. Una falla se registra y no corta nada.
    void borrar(String publicId);

    // Borra el archivo viejo recien cuando la transaccion se confirma.
    void borrarDespuesDelCommit(String publicId);

    // Borra el archivo recien subido si la transaccion se revierte.
    void borrarSiSeRevierte(String publicId);
}
