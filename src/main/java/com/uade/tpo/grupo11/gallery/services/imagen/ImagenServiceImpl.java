package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenRequest;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.ImagenNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

// Logica de negocio de las imagenes: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class ImagenServiceImpl implements ImagenService {

    @Autowired
    private ImagenRepository repoImagen;

    @Autowired
    private ObraRepository obraRepository;


    // Devuelve las imagenes.
    @Override
    public List<Imagen> getImagenes() {
        return repoImagen.findAll();
    }


    // Devuelve imagenes de la obra.
    @Override
    public List<Imagen> getImagenesByObra(Long obraId) {

        // Si la obra no existe avisamos con 404, en lugar de devolver una lista vacia
        // que el cliente podria confundir con "esta obra no tiene imagenes".
        if (!obraRepository.existsById(obraId)) {
            throw new ObraNotFoundException(obraId);
        }

        return repoImagen.findByObraIdOrdenadas(obraId);
    }


    // Busca la imagen por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Imagen getImagenById(Long imagenId) {
        return repoImagen.findById(imagenId)
                .orElseThrow(() -> new ImagenNotFoundException(imagenId));
    }


    // Crea la imagen con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    public Imagen createImagen(ImagenRequest request, Usuario usuarioActual) throws IOException {

        // El archivo no se valida con anotaciones en el Request: @NotNull no detecta
        // un MultipartFile vacio, asi que la regla se controla aca.
        if (request.getArchivo() == null || request.getArchivo().isEmpty()) {
            throw new IllegalArgumentException("El archivo de la imagen es obligatorio");
        }

        Obra obra = obraRepository
                .findById(request.getObra_id())
                .orElseThrow(() -> new ObraNotFoundException(request.getObra_id()));

        OwnershipGuard.verificar(usuarioActual, obra.getArtista().getUsuario().getId());

        Imagen imagen = Imagen.builder()
                .obra(obra)
                .orden_imagen(calcularOrden(request, obra.getId()))
                // getBytes() pasa el archivo subido a byte[], que es lo que espera la columna BLOB.
                .contenido_imagen(request.getArchivo().getBytes())
                .build();

        return repoImagen.save(imagen);
    }


    // Actualiza la imagen: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    public Imagen updateImagen(Long imagenId, ImagenRequest request, Usuario usuarioActual) throws IOException {

        Imagen imagenExistente = getImagenById(imagenId);

        OwnershipGuard.verificar(usuarioActual, imagenExistente.getObra().getArtista().getUsuario().getId());

        // La obra de una imagen no se cambia: la imagen pertenece a la obra donde se subio.
        if (request.getOrden_imagen() != null) {
            imagenExistente.setOrden_imagen(request.getOrden_imagen());
        }

        if (request.getArchivo() != null && !request.getArchivo().isEmpty()) {
            imagenExistente.setContenido_imagen(request.getArchivo().getBytes());
        }

        return repoImagen.save(imagenExistente);
    }


    // Elimina la imagen de la base.
    @Override
    public void deleteImagen(Long imagenId, Usuario usuarioActual) {

        Imagen imagen = getImagenById(imagenId);

        OwnershipGuard.verificar(usuarioActual, imagen.getObra().getArtista().getUsuario().getId());

        repoImagen.delete(imagen);
    }


    // Si el cliente no manda el orden, la imagen se agrega al final de la galeria de esa obra.
    private int calcularOrden(ImagenRequest request, Long obraId) {

        if (request.getOrden_imagen() != null) {
            return request.getOrden_imagen();
        }

        return repoImagen.findByObraIdOrdenadas(obraId).size() + 1;
    }
}
