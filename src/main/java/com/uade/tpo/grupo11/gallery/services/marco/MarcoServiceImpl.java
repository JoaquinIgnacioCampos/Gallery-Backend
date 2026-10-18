package com.uade.tpo.grupo11.gallery.services.marco;

import com.uade.tpo.grupo11.gallery.controllers.marco.MarcoRequest;
import com.uade.tpo.grupo11.gallery.entities.Marco;
import com.uade.tpo.grupo11.gallery.exceptions.MarcoNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.ListaVaciaException;
import com.uade.tpo.grupo11.gallery.repositories.MarcoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import com.uade.tpo.grupo11.gallery.exceptions.RecursoEnUsoException;
import com.uade.tpo.grupo11.gallery.repositories.ItemCarritoRepository;
import com.uade.tpo.grupo11.gallery.repositories.ItemFacturaRepository;
import com.uade.tpo.grupo11.gallery.services.almacenamiento.AlmacenamientoService;

// Logica de negocio de los marcos: valida, resuelve las relaciones y coordina los repositorios.
@Service
public class MarcoServiceImpl implements MarcoService {

    @Autowired
    private MarcoRepository marcoRepository;

    @Autowired
    private AlmacenamientoService almacenamiento;


    // Devuelve los marcos.
    @Override
    public List<Marco> getMarcos() {
        List<Marco> marco = marcoRepository.findAll();

        if (marco.isEmpty()) {
            throw new ListaVaciaException("No hay marcos cargados cargados");
        }

        return marco;
    }


    // Busca el marco por id. Si no existe, se lanza la excepcion y el handler responde 404.
    @Override
    public Marco getMarcoById(Long marcoId) {

        return marcoRepository
                .findById(marcoId)
                .orElseThrow(() -> new MarcoNotFoundException(marcoId));
    }


    // Crea el marco con los datos del request. Las relaciones llegan como ids y se resuelven en el service.
    @Override
    @Transactional
    public Marco createMarco(MarcoRequest request) throws IOException {

        if (request.getArchivo() == null || request.getArchivo().isEmpty()) {
            throw new IllegalArgumentException("El archivo de la imagen del marco es obligatorio");
        }
        almacenamiento.validarImagen(request.getArchivo());

        // La foto se sube a Cloudinary (carpeta de marcos); si la transaccion se revierte, se borra.
        AlmacenamientoService.ImagenSubida subida =
                almacenamiento.subir(request.getArchivo(), AlmacenamientoService.CARPETA_MARCOS);
        almacenamiento.borrarSiSeRevierte(subida.publicId());

        Marco marco = Marco.builder()
                .nombre_marco(request.getNombre_marco())
                .color_marco(request.getColor_marco())
                .url_marco(subida.url())
                .public_id_marco(subida.publicId())
                .precio_marco(request.getPrecio_marco())
                .build();

        return marcoRepository.save(marco);
    }


    // Actualiza el marco: lo trae de la base y le pisa los campos, en vez de guardar lo que llega.
    @Override
    @Transactional
    public Marco updateMarco(
            Long marcoId,
            MarcoRequest request) throws IOException {

        Marco marco = marcoRepository
                .findById(marcoId)
                .orElseThrow(() -> new MarcoNotFoundException(marcoId));

        marco.setNombre_marco(request.getNombre_marco());
        marco.setColor_marco(request.getColor_marco());
        marco.setPrecio_marco(request.getPrecio_marco());

        // Una foto nueva reemplaza a la anterior: la vieja se borra de Cloudinary despues del commit.
        if (request.getArchivo() != null && !request.getArchivo().isEmpty()) {
            almacenamiento.validarImagen(request.getArchivo());

            AlmacenamientoService.ImagenSubida subida =
                    almacenamiento.subir(request.getArchivo(), AlmacenamientoService.CARPETA_MARCOS);
            almacenamiento.borrarSiSeRevierte(subida.publicId());
            almacenamiento.borrarDespuesDelCommit(marco.getPublic_id_marco());

            marco.setUrl_marco(subida.url());
            marco.setPublic_id_marco(subida.publicId());
        }

        return marcoRepository.save(marco);
    }


    // Elimina el marco de la base.
    @Override
    @Transactional
    public void deleteMarco(Long marcoId) {

        Marco marco = marcoRepository
                .findById(marcoId)
                .orElseThrow(() ->
                        new MarcoNotFoundException(marcoId)
                );

        if (
                itemCarritoRepository.existsByMarcoId(marcoId) ||
                        itemFacturaRepository.existsByMarcoId(marcoId)
        ) {
            throw new RecursoEnUsoException(
                    "No se puede eliminar el marco porque está siendo utilizado."
            );
        }

        marcoRepository.delete(marco);

        // La foto se borra de Cloudinary recien cuando la transaccion se confirma.
        almacenamiento.borrarDespuesDelCommit(marco.getPublic_id_marco());
    }

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ItemFacturaRepository itemFacturaRepository;
}
