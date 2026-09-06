package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenRequest;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.exceptions.ImagenNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagenServiceImpl implements ImagenService {

    @Autowired
    private ImagenRepository repoImagen;

    @Autowired
    private ObraRepository obraRepository;


    @Override
    public List<Imagen> getImagenes() {
        return repoImagen.findAll();
    }


    @Override
    public List<Imagen> getImagenesByObra(Long obraId) {

        if (!obraRepository.existsById(obraId)) {
            throw new ObraNotFoundException(obraId);
        }

        return repoImagen.findByObraIdOrdenadas(obraId);
    }


    @Override
    public Imagen getImagenById(Long imagenId) {
        return repoImagen.findById(imagenId)
                .orElseThrow(() -> new ImagenNotFoundException(imagenId));
    }


    @Override
    public Imagen createImagen(ImagenRequest request) {

        Obra obra = obraRepository
                .findById(request.getObra_id())
                .orElseThrow(() -> new ObraNotFoundException(request.getObra_id()));

        Imagen imagen = Imagen.builder()
                .obra(obra)
                .orden_imagen(calcularOrden(request, obra.getId()))
                .contenido_imagen(request.getContenido_imagen())
                .build();

        return repoImagen.save(imagen);
    }


    @Override
    public Imagen updateImagen(Long imagenId, ImagenRequest request) {

        Imagen imagenExistente = getImagenById(imagenId);

        // La obra de una imagen no se cambia: la imagen pertenece a la obra donde se subio.
        if (request.getOrden_imagen() != null) {
            imagenExistente.setOrden_imagen(request.getOrden_imagen());
        }

        if (request.getContenido_imagen() != null) {
            imagenExistente.setContenido_imagen(request.getContenido_imagen());
        }

        return repoImagen.save(imagenExistente);
    }


    @Override
    public void deleteImagen(Long imagenId) {

        Imagen imagen = getImagenById(imagenId);

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
