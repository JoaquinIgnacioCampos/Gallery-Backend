package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.io.IOException;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// Intercepta las excepciones de TODOS los controllers y las traduce a códigos HTTP.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObraNotFoundException.class)
    public ResponseEntity<String> handleObraNotFound(ObraNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }


    @ExceptionHandler(VarianteNotFoundException.class)
    public ResponseEntity<String> handleVarianteNotFound(VarianteNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }


    @ExceptionHandler(MensajeNotFoundException.class)
    public ResponseEntity<String> handleMensajeNotFound(MensajeNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }


    @ExceptionHandler(EncargoNotFoundException.class)
    public ResponseEntity<String> handleEncargoNotFound(EncargoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }


  @ExceptionHandler(ImagenNotFoundException.class)
    public ResponseEntity<String> handleImagenNotFound(ImagenNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }
  

    @ExceptionHandler(PerfilArtistaNotFoundException.class)
    public ResponseEntity<String> handlePerfilArtistaNotFound(PerfilArtistaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(PerfilArtistaNoAceptaEncargosException.class)
    public ResponseEntity<String> handlePerfilArtistaNoAceptaEncargos(PerfilArtistaNoAceptaEncargosException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }


    @ExceptionHandler(ItemFacturaNotFoundException.class)
    public ResponseEntity<String> handleItemFacturaNotFound(ItemFacturaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<String> handleStockInsuficiente(StockInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());  // 409, no 404
    }

    @ExceptionHandler(EstiloNotFoundException.class)
    public ResponseEntity<String> handleEstiloNotFound(EstiloNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // Se dispara cuando un @Valid falla: junta todos los mensajes de los campos que faltan.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidacion(MethodArgumentNotValidException e) {
        String mensajes = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);  // 400
    }

    @ExceptionHandler(CarritoVacioException.class)
    public ResponseEntity<String> handleCarritoVacio(CarritoVacioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());  // 409
    }

    @ExceptionHandler(ObraEnUsoException.class)
    public ResponseEntity<String> handleObraEnUso(ObraEnUsoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());  // 409: choca con el estado actual
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleDatosInvalidos(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // 400: el cliente mando datos invalidos
    }

    @ExceptionHandler(EstiloDuplicadoException.class)
    public ResponseEntity<String> handleEstiloDuplicado(EstiloDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<String> handleTransicionInvalida(TransicionEstadoInvalidaException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(CarritoNotFoundException.class)
    public ResponseEntity<String> handleCarritoNotFound(CarritoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CompraNotFoundException.class)
    public ResponseEntity<String> handleCompraNotFound(CompraNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(FacturaNotFoundException.class)
    public ResponseEntity<String> handleFacturaNotFound(FacturaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ItemCarritoNotFoundException.class)
    public ResponseEntity<String> handleItemCarritoNotFound(ItemCarritoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MarcoNotFoundException.class)
    public ResponseEntity<String> handleMarcoNotFound(MarcoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TamanioLienzoNotFoundException.class)
    public ResponseEntity<String> handleTamanioLienzoNotFound(TamanioLienzoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<String> handleUsuarioNotFound(UsuarioNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatePerfilArtistaException.class)
    public ResponseEntity<String> handleDuplicatePerfilArtista(DuplicatePerfilArtistaException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateUserMailException.class)
    public ResponseEntity<String> handleDuplicateUserMail(DuplicateUserMailException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<String> handleDuplicateUsername(DuplicateUsernameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(PerfilArtistaInvalidDataException.class)
    public ResponseEntity<String> handlePerfilArtistaInvalidData(PerfilArtistaInvalidDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mail o contraseña incorrectos");
    }
  
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("El archivo supera el tamaño máximo permitido");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo procesar el archivo enviado");
    }

}
