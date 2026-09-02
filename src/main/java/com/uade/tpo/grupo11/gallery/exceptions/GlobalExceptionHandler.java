package com.uade.tpo.grupo11.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Intercepta las excepciones de TODOS los controllers y las traduce a códigos HTTP.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObraNotFoundException.class)
    public ResponseEntity<String> handleObraNotFound(ObraNotFoundException e) {
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

    @ExceptionHandler(ArtistaNotFoundException.class)
    public ResponseEntity<String> handleArtistaNotFound(ArtistaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ArtistaNoAceptaEncargosException.class)
    public ResponseEntity<String> handleArtistaNoAceptaEncargos(ArtistaNoAceptaEncargosException e) {
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

}
