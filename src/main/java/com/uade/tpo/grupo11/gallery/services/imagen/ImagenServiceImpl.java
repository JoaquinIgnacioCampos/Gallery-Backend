package com.uade.tpo.grupo11.gallery.services.imagen;

import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Variante;
import com.uade.tpo.grupo11.gallery.exceptions.ImagenNotFoundException;
import com.uade.tpo.grupo11.gallery.exceptions.VarianteNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ImagenRepository;
import com.uade.tpo.grupo11.gallery.repositories.VarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagenServiceImpl implements ImagenService {
    @Autowired
    private ImagenRepository repoImagen;

    @Override
    public List<Imagen> getImagenes() {
        return repoImagen.findAll();
    }

    @Override
    public Imagen getImagenById(Long imagenId) {
        return repoImagen.findById(imagenId)
                .orElseThrow(() -> new ImagenNotFoundException(imagenId));
    }

    @Override
    public Imagen createImagen(Imagen imagen) {
        return repoImagen.save(imagen);
    }

    @Override
    public Imagen updateImagen(Long imagenId, Imagen imagen) {

        Imagen imagenExistente = getImagenById(imagenId);

        imagenExistente.setObra(imagen.getObra());
        imagenExistente.setOrden_imagen(imagen.getOrden_imagen());
        imagenExistente.setContenido_imagen(imagen.getContenido_imagen());

        return repoImagen.save(imagenExistente);
    }

    @Override
    public void deleteImagen(Long imagenId) {
        Imagen imagen = getImagenById(imagenId);
        //Se comprueba existencia de Obra
        repoImagen.delete(imagen);

    }
}
