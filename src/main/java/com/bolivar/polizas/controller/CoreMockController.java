package com.bolivar.polizas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Mock (simulacion) del CORE transaccional legado.
// Su unico proposito: registrar en logs que la operacion se intento enviar al CORE.
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    // POST /core-mock/evento   body: {"evento": "ACTUALIZACION", "polizaId": 555}
    @PostMapping("/evento")
    public Map<String, String> recibirEvento(@RequestBody Map<String, Object> evento) {
        log.info("CORE-MOCK: se intento enviar al CORE el evento={} polizaId={}",
                evento.get("evento"), evento.get("polizaId"));
        return Map.of("resultado", "REGISTRADO");
    }
}
