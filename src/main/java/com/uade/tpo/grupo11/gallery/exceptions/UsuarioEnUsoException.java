package com.uade.tpo.grupo11.gallery.exceptions;

// Se lanza al intentar dar de baja un usuario que ya tiene compras registradas.
// No se borra: la compra es un documento historico y perderia a su duenio.
public class UsuarioEnUsoException extends RuntimeException {

    public UsuarioEnUsoException(Long usuarioId) {
        super("No se puede eliminar el usuario con id " + usuarioId
                + ": tiene compras registradas y son documentos historicos");
    }
}
