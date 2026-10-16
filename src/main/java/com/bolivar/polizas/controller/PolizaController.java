package com.bolivar.polizas.controller;

import com.bolivar.polizas.model.EstadoPoliza;
import com.bolivar.polizas.model.Poliza;
import com.bolivar.polizas.model.Riesgo;
import com.bolivar.polizas.model.TipoPoliza;
import com.bolivar.polizas.service.PolizaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// @RestController: clase que recibe peticiones HTTP y responde JSON.
// @RequestMapping("/polizas"): todas las rutas de esta clase empiezan con /polizas.
// El controller NO tiene logica de negocio: solo recibe, delega al service y responde.
@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    // GET /polizas?tipo=COLECTIVA&estado=ACTIVA
    // @RequestParam(required = false): los filtros son opcionales.
    // Spring convierte el texto "COLECTIVA" al enum automaticamente.
    @GetMapping
    public List<Poliza> listar(@RequestParam(required = false) TipoPoliza tipo,
                               @RequestParam(required = false) EstadoPoliza estado) {
        return polizaService.listar(tipo, estado);
    }

    // GET /polizas/2/riesgos. @PathVariable toma el {id} de la URL.
    @GetMapping("/{id}/riesgos")
    public List<Riesgo> riesgos(@PathVariable Long id) {
        return polizaService.listarRiesgos(id);
    }

    // POST /polizas/1/renovar
    @PostMapping("/{id}/renovar")
    public Poliza renovar(@PathVariable Long id) {
        return polizaService.renovar(id);
    }

    // POST /polizas/1/cancelar
    @PostMapping("/{id}/cancelar")
    public Poliza cancelar(@PathVariable Long id) {
        return polizaService.cancelar(id);
    }

    // POST /polizas/2/riesgos   body: {"descripcion": "Apartamento 201"}
    // @RequestBody convierte el JSON del cuerpo en un Map.
    // @ResponseStatus CREATED: responde 201 porque se creo un recurso nuevo.
    @PostMapping("/{id}/riesgos")
    @ResponseStatus(HttpStatus.CREATED)
    public Riesgo agregarRiesgo(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return polizaService.agregarRiesgo(id, body.get("descripcion"));
    }
}
