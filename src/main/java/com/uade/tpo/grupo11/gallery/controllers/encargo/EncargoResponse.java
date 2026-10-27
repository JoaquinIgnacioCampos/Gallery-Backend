package com.uade.tpo.grupo11.gallery.controllers.encargo;

import com.uade.tpo.grupo11.gallery.entities.Encargo;
import com.uade.tpo.grupo11.gallery.entities.enums.EstadoEncargo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoLienzo;
import com.uade.tpo.grupo11.gallery.entities.enums.TipoPintura;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Lo que la API devuelve de los encargos. No exponemos la entidad: evita recursion y datos de mas.
// Ademas de los ids viajan los nombres y los textos ya resueltos (cliente, artista, tamanio, marco, tipos y
// estado) y los estados a los que se puede pasar: si no, para dibujar un encargo el front tendria que pedir
// cada dato por separado y repetir las listas y las reglas del back.
public record EncargoResponse(
        Long id,
        Long artista_id,
        Long usuario_id,
        Long tamanio_id,
        Long marco_id,
        TipoPintura tipo_pintura,
        TipoLienzo tipo_lienzo,
        EstadoEncargo estado_encargo,
        String descripcion_encargo,
        BigDecimal precio_acordado,
        LocalDateTime fecha_creacion_encargo,
        String nombre_cliente,
        String nombre_artista,
        Long artista_usuario_id,
        String nombre_tamanio,
        String nombre_marco,
        String nombre_tipo_pintura,
        String nombre_tipo_lienzo,
        String nombre_estado,
        List<OpcionResponse> estados_siguientes
) {
    // Traduce la entidad a lo que ve el cliente. El nombre del cliente llega aparte (lo resuelve EncargoResponder).
    public static EncargoResponse fromEntity(Encargo encargo, String nombreCliente) {
        return new EncargoResponse(
                encargo.getId(),
                encargo.getArtista().getId(),
                encargo.getUsuario().getId(),
                encargo.getTamanio().getId(),
                encargo.getMarco().getId(),
                encargo.getTipo_pintura(),
                encargo.getTipo_lienzo(),
                encargo.getEstado_encargo(),
                encargo.getDescripcion_encargo(),
                encargo.getPrecio_acordado(),
                encargo.getFecha_creacion_encargo(),
                nombreCliente,
                encargo.getArtista().getNombre_artistico(),
                encargo.getArtista().getUsuario().getId(),
                encargo.getTamanio().getNombre_tamanio(),
                encargo.getMarco().getNombre_marco(),
                encargo.getTipo_pintura().getNombre(),
                encargo.getTipo_lienzo().getNombre(),
                encargo.getEstado_encargo().getNombre(),
                encargo.getEstado_encargo().estadosSiguientes().stream()
                        .map(estado -> new OpcionResponse(estado.name(), estado.getNombre()))
                        .toList()
        );
    }
}
