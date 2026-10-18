package com.uade.tpo.grupo11.gallery.services.almacenamiento;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uade.tpo.grupo11.gallery.exceptions.AlmacenamientoImagenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

// Guarda y borra los archivos de las imagenes en Cloudinary. En la base solo queda la URL
// y el public_id (el identificador que Cloudinary necesita para borrar el archivo).
// La URL de conexion (cloudinary://<api_key>:<api_secret>@<cloud_name>) vive en
// application.properties, que no se versiona: el api_secret no puede llegar al repositorio.
@Service
public class AlmacenamientoServiceImpl implements AlmacenamientoService {

    private static final Logger log = LoggerFactory.getLogger(AlmacenamientoServiceImpl.class);

    // null si no hay credenciales: la aplicacion arranca igual y falla con un mensaje claro
    // recien cuando alguien intenta subir una foto.
    private final Cloudinary cloudinary;

    public AlmacenamientoServiceImpl(@Value("${cloudinary.url:}") String cloudinaryUrl) {
        this.cloudinary = cloudinaryUrl.isBlank() ? null : new Cloudinary(cloudinaryUrl);
    }


    // Solo se aceptan fotos. El tipo que declara el cliente (Content-Type) no se puede creer, asi que se
    // mira el contenido: los primeros bytes de un JPG, PNG, GIF o WEBP son siempre los mismos. Un archivo
    // que no es una imagen se rechaza con un 400 y no llega a Cloudinary.
    @Override
    public void validarImagen(MultipartFile archivo) throws IOException {
        byte[] c = archivo.getInputStream().readNBytes(12);

        boolean jpg = c.length >= 3 && c[0] == (byte) 0xFF && c[1] == (byte) 0xD8 && c[2] == (byte) 0xFF;
        boolean png = c.length >= 4 && c[0] == (byte) 0x89 && c[1] == 'P' && c[2] == 'N' && c[3] == 'G';
        boolean gif = c.length >= 4 && c[0] == 'G' && c[1] == 'I' && c[2] == 'F' && c[3] == '8';
        boolean webp = c.length >= 12 && c[0] == 'R' && c[1] == 'I' && c[2] == 'F' && c[3] == 'F'
                && c[8] == 'W' && c[9] == 'E' && c[10] == 'B' && c[11] == 'P';

        if (!(jpg || png || gif || webp)) {
            throw new IllegalArgumentException("El archivo tiene que ser una imagen (JPG, PNG, WEBP o GIF)");
        }
    }


    // Sube el archivo a la carpeta indicada y devuelve su URL (https) y su public_id.
    @Override
    public ImagenSubida subir(MultipartFile archivo, String carpeta) throws IOException {
        Map<?, ?> resultado;
        try {
            resultado = clienteConfigurado().uploader().upload(archivo.getBytes(),
                    ObjectUtils.asMap("folder", carpeta, "resource_type", "image"));
        } catch (AlmacenamientoImagenException | IOException e) {
            throw e;
        } catch (RuntimeException e) {
            // El detalle (credenciales invalidas, cuota, caida) es para quien administra el sistema:
            // al cliente le llega un 502 generico y la causa queda en el log.
            log.error("Cloudinary rechazo la subida de una imagen", e);
            throw new AlmacenamientoImagenException("No se pudo guardar la imagen en el servicio de imagenes", e);
        }

        Object url = resultado.get("secure_url");
        Object publicId = resultado.get("public_id");
        if (url == null || publicId == null) {
            throw new AlmacenamientoImagenException("El servicio de imagenes no devolvio la direccion de la imagen");
        }
        return new ImagenSubida(url.toString(), publicId.toString());
    }


    // Borra el archivo. Si no existe ya, no es un problema. Una falla se registra y no corta nada:
    // a lo sumo queda un archivo huerfano en Cloudinary, que no rompe la aplicacion.
    @Override
    public void borrar(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        try {
            clienteConfigurado().uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "image", "invalidate", true));
        } catch (Exception e) {
            log.warn("No se pudo borrar la imagen {} de Cloudinary: queda huerfana", publicId, e);
        }
    }


    // Borra el archivo viejo recien cuando la transaccion se confirma: si la base falla,
    // la fila sigue apuntando a ese archivo y no puede haberse borrado. Sin transaccion
    // en curso no hay nada que esperar y se borra enseguida.
    @Override
    public void borrarDespuesDelCommit(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            borrar(publicId);
            return;
        }
        alTerminar(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                borrar(publicId);
            }
        });
    }


    // Borra el archivo recien subido si la transaccion se revierte: la base no quedo con su URL.
    @Override
    public void borrarSiSeRevierte(String publicId) {
        alTerminar(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int estado) {
                if (estado != STATUS_COMMITTED) {
                    borrar(publicId);
                }
            }
        });
    }


    private void alTerminar(TransactionSynchronization accion) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(accion);
        }
    }


    private Cloudinary clienteConfigurado() {
        if (cloudinary == null) {
            throw new AlmacenamientoImagenException(
                    "El servicio de imagenes no esta configurado (falta cloudinary.url en application.properties)");
        }
        return cloudinary;
    }
}
