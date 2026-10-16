package com.bolivar.polizas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

// Adaptador de integracion con el CORE (el servicio de edicion en WebLogic).
// Aisla al resto de la app de COMO se llama al CORE: si cambia la URL o el protocolo,
// solo se toca esta clase.
@Component
public class CoreClient {

    private static final Logger log = LoggerFactory.getLogger(CoreClient.class);

    private final RestClient restClient;

    // @Value lee los valores de application.properties (core.url y api.key)
    public CoreClient(@Value("${core.url}") String coreUrl,
                      @Value("${api.key}") String apiKey) {
        // RestClient: cliente HTTP de Spring. Se configura una vez con la URL base y el header.
        this.restClient = RestClient.builder()
                .baseUrl(coreUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    // Envia POST /core-mock/evento con {"evento": "ACTUALIZACION", "polizaId": 1}
    public void notificar(String evento, Long polizaId) {
        try {
            restClient.post()
                    .uri("/core-mock/evento")
                    .body(Map.of("evento", evento, "polizaId", polizaId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            // Tolerancia a fallos: si el CORE no responde NO se cae la operacion del usuario.
            // Se registra en log. En produccion aqui iria un reintento o una cola (outbox).
            log.warn("No se pudo notificar al CORE. evento={} polizaId={} causa={}",
                    evento, polizaId, e.getMessage());
        }
    }
}
