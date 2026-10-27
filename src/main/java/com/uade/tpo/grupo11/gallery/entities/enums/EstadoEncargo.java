package com.uade.tpo.grupo11.gallery.entities.enums;

import java.util.List;

public enum EstadoEncargo {
    PENDIENTE("Pendiente"),
    APROBADO("Aprobado"),
    EN_PROCESO("En proceso"),
    TERMINADO("Terminado"),
    PAGADO("Pagado"),
    CANCELADO("Cancelado");

    // El texto que ve la persona. Vive acá para que el front no repita la lista.
    private final String nombre;

    EstadoEncargo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // A que estados se puede pasar desde este. Solo se cancela desde PENDIENTE: aprobado y en proceso ya no.
    // Volver a PENDIENTE reabre la negociacion del precio. TERMINADO pasa a PAGADO solo con el pago;
    // PAGADO y CANCELADO son finales. Es la unica copia de la regla: el service la usa para validar y el
    // front la recibe en cada encargo para mostrar solo los cambios posibles.
    public List<EstadoEncargo> estadosSiguientes() {
        return switch (this) {
            case PENDIENTE -> List.of(CANCELADO);
            case APROBADO -> List.of(EN_PROCESO, PENDIENTE);
            case EN_PROCESO -> List.of(TERMINADO, PENDIENTE);
            case TERMINADO, PAGADO, CANCELADO -> List.of();
        };
    }
}
