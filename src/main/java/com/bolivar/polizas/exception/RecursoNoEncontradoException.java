package com.bolivar.polizas.exception;

// Error propio para "no existe". extends RuntimeException = no obliga a declararlo con throws.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
