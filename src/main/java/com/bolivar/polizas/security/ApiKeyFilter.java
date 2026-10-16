package com.bolivar.polizas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filtro HTTP: se ejecuta ANTES de que la peticion llegue a cualquier controller.
// OncePerRequestFilter garantiza que corre una sola vez por peticion.
// @Component: Spring lo detecta y lo registra automaticamente.
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String apiKey;

    // La llave se lee de application.properties, no esta quemada en el codigo
    public ApiKeyFilter(@Value("${api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("x-api-key");

        // apiKey.equals(header) y no al reves: si header es null no lanza error
        if (!apiKey.equals(header)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"x-api-key invalida o ausente\"}");
            return; // corta aqui: la peticion NO sigue al controller
        }

        // Llave correcta: deja pasar la peticion al siguiente paso
        chain.doFilter(request, response);
    }
}
