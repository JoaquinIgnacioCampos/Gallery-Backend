package com.uade.tpo.grupo11.gallery.services;

import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.exceptions.ObraNotFoundException;
import com.uade.tpo.grupo11.gallery.repositories.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service: acá sí va. Spring crea este objeto y se lo inyecta a quien pida un ObraService.
@Service
public class ObraServiceImpl implements ObraService {

    @Autowired
    private ObraRepository repoObra;

    //GET
    @Override
    public Obra getObraById(Long obraId) {
        // findById devuelve Optional: o está, o lanzamos el error con nombre propio.
        return repoObra.findById(obraId)
                .orElseThrow(() -> new ObraNotFoundException(obraId));
    }

    @Override
    public List<Obra> getListObras() {
        return repoObra.findAll();
    }//Repository trae TODAS las Obras.

    //POST
    @Override
    public Obra createObra(Obra obra) {
        return repoObra.save(obra);
    }//Guarda ésta Obra y devoolveme la Obra guardada

    //PUT
    @Override
    public Obra modificarObra(Long ObraId, Obra obra) {
        Obra obraExistente = getObraById(obraId);
        //Busca Obra existente
        obraExistente.setTitulo(obra.getTitulo());
        //Obra existente ponele  título que vino en el objeto nuevo.
       obraExistente.setDescripcion(obra.getDescripcion());
       //Obra existente ponele descripción que vino en el objeto nuevo.

        return repoObra.save(obraExistente);
        //Guardá los cambios.
    }

    //DELETE
    @Override
    public void eliminarObra(Long obraId) {
        Obra obra = getObraById(obraId);
        //Se comprueba existencia de Obra
        repoObra.delete(obra);
        //Se elimina
        //void --> no retorna Nada!

    }
}