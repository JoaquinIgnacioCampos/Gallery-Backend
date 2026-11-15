package com.uade.tpo.grupo11.gallery.controllers.obra;

import com.uade.tpo.grupo11.gallery.controllers.imagen.ImagenResponse;
import com.uade.tpo.grupo11.gallery.controllers.variante.VarianteResponse;
import com.uade.tpo.grupo11.gallery.entities.Estilo;
import com.uade.tpo.grupo11.gallery.entities.Imagen;
import com.uade.tpo.grupo11.gallery.entities.Obra;
import com.uade.tpo.grupo11.gallery.entities.Variante;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Lo que la API devuelve de las obras. No exponemos la entidad: evita recursion y datos de mas.
// Cada obra viaja completa: con sus variantes (tamanio, precio, stock y descuento) y sus imagenes en orden (la primera
// es la portada). Una lista de obras alcanza para armar las tarjetas y el detalle, sin un pedido por obra. Hibernate
// trae las variantes, las imagenes y los estilos de toda la pagina en una consulta cada uno (@BatchSize en Obra).
public record ObraResponse(
        Long id,
        String nombre_obra,
        String descripcion_obra,
        boolean en_venta,
        boolean eliminada,
        Long artista_id,
        Set<Long> estilo_ids,
        List<VarianteResponse> variantes,
        List<ImagenResponse> imagenes
) {
    // Traduce la entidad a lo que ve el cliente. Manda ids en vez de objetos anidados.
    public static ObraResponse fromEntity(Obra obra) {
        return new ObraResponse(
                obra.getId(),
                obra.getNombre_obra(),
                obra.getDescripcion_obra(),
                obra.isEn_venta(),
                obra.isEliminada(),
                obra.getArtista().getId(),
                obra.getEstilos().stream().map(Estilo::getId).collect(Collectors.toSet()),
                obra.getVariantes().stream()
                        .sorted(Comparator.comparing(Variante::getId))
                        .map(VarianteResponse::fromEntity)
                        .toList(),
                obra.getImagenes().stream()
                        .sorted(Comparator.comparingInt(Imagen::getOrden_imagen))
                        .map(ImagenResponse::fromEntity)
                        .toList()
        );
    }
}
