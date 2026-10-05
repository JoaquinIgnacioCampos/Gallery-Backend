package com.uade.tpo.grupo11.gallery.services.imagen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenRequest;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.exceptions.ImagenNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.RecursoNoEncontradoException;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import com.uade.tpo.grupo11.gallery.security.OwnershipGuard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<Imagen> getImagenes(PageRequest pageable) {
        Page<Imagen> imagenes = repoImagen.findAll(pageable);
        if (imagenes.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay imagenes registradas en el sistema");
        }
        return imagenes;
    }


    // Devuelve imagenes de la obra.
    @Override
    public Page<Imagen> getImagenesByObra(Long obraId, PageRequest pageable) {

        if (!obraRepository.existsById(obraId)) {
            throw new ObraNotFoundException(obraId);
        }

        Page<Imagen> imagenes = repoImagen.findByObraIdOrdenadas(obraId, pageable);
        if (imagenes.isEmpty()) {
            throw new RecursoNoEncontradoException("La obra con id " + obraId + " no tiene imagenes");
        }
        return imagenes;
    }


    // Busca la imagen por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Imagen getImagenById(Long imagenId) {
        return repoImagen.findById(imagenId)
                .orElseThrow(() -> new ImagenNotFoundException(imagenId));
    }


    // Crea la imagen con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    @Transactional
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

        List<Imagen> galeria = repoImagen.findByObraIdOrdenadas(obra.getId());
        int posicion = resolverPosicionAlta(request.getOrden_imagen(), galeria.size());

        // Hacen lugar: las que estaban desde esa posicion en adelante se corren una lugar.
        for (Imagen existente : galeria) {
            if (existente.getOrden_imagen() >= posicion) {
                existente.setOrden_imagen(existente.getOrden_imagen() + 1);
            }
        }

        Imagen imagen = Imagen.builder()
                .obra(obra)
                .orden_imagen(posicion)
                // getBytes() pasa el archivo subido a byte[], que es lo que espera la columna BLOB.
                .contenido_imagen(request.getArchivo().getBytes())
                .build();

        return repoImagen.save(imagen);
    }


    // Actualiza la imagen: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    @Transactional
    public Imagen updateImagen(Long imagenId, ImagenRequest request, Usuario usuarioActual) throws IOException {

        Imagen imagenExistente = getImagenById(imagenId);

        OwnershipGuard.verificar(usuarioActual, imagenExistente.getObra().getArtista().getUsuario().getId());

        // La obra de una imagen no se cambia: la imagen pertenece a la obra donde se subio.
        if (request.getOrden_imagen() != null) {
            moverA(imagenExistente, request.getOrden_imagen());
        }

        if (request.getArchivo() != null && !request.getArchivo().isEmpty()) {
            imagenExistente.setContenido_imagen(request.getArchivo().getBytes());
        }

        return repoImagen.save(imagenExistente);
    }


    // Elimina la imagen de la base.
    @Override
    @Transactional
    public void deleteImagen(Long imagenId, Usuario usuarioActual) {

        Imagen imagen = getImagenById(imagenId);

        OwnershipGuard.verificar(usuarioActual, imagen.getObra().getArtista().getUsuario().getId());

        int posicionBorrada = imagen.getOrden_imagen();
        Long obraId = imagen.getObra().getId();
        repoImagen.delete(imagen);

        // Cierran el hueco: las que venian despues bajan una posicion.
        for (Imagen restante : repoImagen.findByObraIdOrdenadas(obraId)) {
            if (restante.getOrden_imagen() > posicionBorrada) {
                restante.setOrden_imagen(restante.getOrden_imagen() - 1);
            }
        }
    }


    // Sin posicion pedida, la imagen va al final de la galeria. Con posicion, puede ir hasta el final (n + 1).
    private int resolverPosicionAlta(Integer posicionPedida, int cantidadActual) {

        if (posicionPedida == null) {
            return cantidadActual + 1;
        }

        if (posicionPedida < 1 || posicionPedida > cantidadActual + 1) {
            throw new IllegalArgumentException(
                    "La posicion tiene que estar entre 1 y " + (cantidadActual + 1));
        }

        return posicionPedida;
    }

    // Mueve la imagen a la nueva posicion y corre las demas para que la galeria siga siendo 1..n sin huecos ni repetidos.
    private void moverA(Imagen imagen, int nuevaPosicion) {

        List<Imagen> galeria = repoImagen.findByObraIdOrdenadas(imagen.getObra().getId());

        if (nuevaPosicion < 1 || nuevaPosicion > galeria.size()) {
            throw new IllegalArgumentException(
                    "La posicion tiene que estar entre 1 y " + galeria.size());
        }

        int posicionActual = imagen.getOrden_imagen();
        if (nuevaPosicion == posicionActual) {
            return;
        }

        for (Imagen otra : galeria) {
            if (otra.getId().equals(imagen.getId())) {
                continue;
            }
            int orden = otra.getOrden_imagen();
            if (nuevaPosicion < posicionActual && orden >= nuevaPosicion && orden < posicionActual) {
                otra.setOrden_imagen(orden + 1);
            } else if (nuevaPosicion > posicionActual && orden > posicionActual && orden <= nuevaPosicion) {
                otra.setOrden_imagen(orden - 1);
            }
        }

        imagen.setOrden_imagen(nuevaPosicion);
    }
}
