package com.bolivar.polizas.controller;

import com.bolivar.polizas.model.Riesgo;
import com.bolivar.polizas.service.PolizaService;
import org.springframework.web.bind.annotation.*;

// Rutas que empiezan con /riesgos
@RestController
@RequestMapping("/riesgos")
public class RiesgoController {

    private final PolizaService polizaService;

    public RiesgoController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    // POST /riesgos/2/cancelar
    @PostMapping("/{id}/cancelar")
    public Riesgo cancelar(@PathVariable Long id) {
        return polizaService.cancelarRiesgo(id);
    }
}
