package com.bolivar.polizas.exception;

// Error propio para "la operacion no esta permitida por las reglas del negocio".
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
