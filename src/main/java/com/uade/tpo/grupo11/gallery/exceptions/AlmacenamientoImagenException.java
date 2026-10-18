package com.uade.tpo.grupo11.gallery.exceptions;

// El servicio de imagenes (Cloudinary) no pudo guardar o borrar un archivo, o no esta configurado.
// El handler responde 502: el problema no esta en lo que mando el cliente sino en un servicio externo.
public class AlmacenamientoImagenException extends RuntimeException {

    public AlmacenamientoImagenException(String mensaje) {
        super(mensaje);
    }

    public AlmacenamientoImagenException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
