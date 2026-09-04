package com.uade.tpo.grupo11.gallery.services.variante;

import com.uade.tpo.grupo11.gallery.entities.Variante;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface VarianteService {

  List<Variante> getListVariante();
  Variante  getVarianteById(Long varianteId);
  Variante createVariante(Variante variante);
  Variante modificarVariante(Long varianteId, Variante variante);
  void eliminarVariante(Long varianteId);

}
