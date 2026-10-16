package com.bolivar.polizas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// @RestControllerAdvice: manejador GLOBAL de errores.
// Cualquier excepcion lanzada en un controller o service llega aqui,
// asi no hay try/catch repetidos en cada endpoint.
@RestControllerAdvice
public class ManejadorErrores {

    // No existe -> 404 Not Found  {"error": "Poliza 99 no existe"}
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    // Regla violada -> 422 Unprocessable Entity: la peticion esta bien formada,
    // pero el negocio no permite la operacion.
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", e.getMessage()));
    }
}
