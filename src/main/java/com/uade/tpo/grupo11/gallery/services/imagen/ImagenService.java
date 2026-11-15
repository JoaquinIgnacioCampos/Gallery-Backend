package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenRequest;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.repositories.ImagenPortada;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// Contrato: que sabe hacer el servicio de las imagenes. La implementacion es la que lleva la logica.
public interface ImagenService {
    // Devuelve las imagenes.
    List<Imagen> getImagenes();
    // Devuelve imagenes de la obra.
    List<Imagen> getImagenesByObra(Long obraId);
    // Devuelve solo la portada (la imagen principal) de cada obra; si se pasan ids, solo de esas obras.
    List<ImagenPortada> getPortadas(List<Long> obraIds);
    // Busca la imagen por id. Si no existe, se lanza la excepcion y el handler responde 404.
    Imagen getImagenById(Long imagenId);
    Imagen createImagen(ImagenRequest request, Usuario usuarioActual) throws IOException;
    Imagen updateImagen(Long imagenId, ImagenRequest request, Usuario usuarioActual) throws IOException;
    void deleteImagen(Long imagenId, Usuario usuarioActual);

    // Deja la galeria de la obra exactamente como llega, en una sola transaccion (todo o nada): en el orden de
    // "orden" van las fotos que se quedan (por id) y las nuevas ("nueva-N" es el archivo N de "nuevas"); las que no
    // estan se borran. "reemplazoIds" y "reemplazos" van de a pares: la foto y su archivo nuevo.
    List<Imagen> reemplazarGaleria(Long obraId, List<String> orden, List<MultipartFile> nuevas,
                                   List<Long> reemplazoIds, List<MultipartFile> reemplazos,
                                   Usuario usuarioActual) throws IOException;
}
