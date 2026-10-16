package com.bolivar.polizas.service;

import com.bolivar.polizas.exception.RecursoNoEncontradoException;
import com.bolivar.polizas.exception.ReglaNegocioException;
import com.bolivar.polizas.model.*;
import com.bolivar.polizas.repository.PolizaRepository;
import com.bolivar.polizas.repository.RiesgoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// @Service: aqui vive la logica de negocio. El controller solo recibe y responde;
// las reglas se validan aqui.
@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreClient coreClient;
    private final BigDecimal ipc;

    // Inyeccion de dependencias por constructor: Spring entrega los repositorios,
    // el CoreClient y el IPC. Facilita las pruebas porque se pueden reemplazar por mocks.
    public PolizaService(PolizaRepository polizaRepository,
                         RiesgoRepository riesgoRepository,
                         CoreClient coreClient,
                         @Value("${polizas.ipc}") BigDecimal ipc) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.coreClient = coreClient;
        this.ipc = ipc;
    }

    // GET /polizas?tipo=&estado=
    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.buscar(tipo, estado);
    }

    // GET /polizas/{id}/riesgos. Primero valida que la poliza exista (si no, 404).
    public List<Riesgo> listarRiesgos(Long polizaId) {
        buscarPoliza(polizaId);
        return riesgoRepository.findByPolizaId(polizaId);
    }

    // POST /polizas/{id}/renovar
    // @Transactional: todo se guarda junto o nada se guarda. Al terminar el metodo,
    // JPA detecta los cambios en la entidad y hace el UPDATE solo (no hace falta save()).
    @Transactional
    public Poliza renovar(Long polizaId) {
        Poliza poliza = buscarPoliza(polizaId);

        // REGLA: no se puede renovar una poliza cancelada
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se puede renovar una poliza cancelada");
        }

        // factor = 1 + IPC/100. Con IPC 5.20 -> 1.052
        BigDecimal factor = BigDecimal.ONE.add(ipc.divide(BigDecimal.valueOf(100)));
        // Nuevo canon = canon x factor, redondeado a 2 decimales
        BigDecimal nuevoCanon = poliza.getCanonMensual().multiply(factor).setScale(2, RoundingMode.HALF_UP);
        // Prima = canon mensual x numero de meses de vigencia
        BigDecimal nuevaPrima = nuevoCanon.multiply(BigDecimal.valueOf(poliza.getMesesVigencia()));

        poliza.setCanonMensual(nuevoCanon);
        poliza.setPrima(nuevaPrima);
        // Misma vigencia inicial: arranca donde termino la anterior
        poliza.setFechaInicio(poliza.getFechaFin());
        poliza.setFechaFin(poliza.getFechaFin().plusMonths(poliza.getMesesVigencia()));
        poliza.setEstado(EstadoPoliza.RENOVADA);

        // Toda accion que cambia estado se notifica al CORE
        coreClient.notificar("ACTUALIZACION", polizaId);
        return poliza;
    }

    // POST /polizas/{id}/cancelar
    @Transactional
    public Poliza cancelar(Long polizaId) {
        Poliza poliza = buscarPoliza(polizaId);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("La poliza ya esta cancelada");
        }

        poliza.setEstado(EstadoPoliza.CANCELADA);
        // REGLA: cancelar la poliza cancela TODOS sus riesgos
        riesgoRepository.findByPolizaId(polizaId)
                .forEach(riesgo -> riesgo.setEstado(EstadoRiesgo.CANCELADO));

        coreClient.notificar("ACTUALIZACION", polizaId);
        return poliza;
    }

    // POST /polizas/{id}/riesgos
    @Transactional
    public Riesgo agregarRiesgo(Long polizaId, String descripcion) {
        Poliza poliza = buscarPoliza(polizaId);

        // REGLAS: agregar riesgo exige validar el tipo; solo colectivas.
        // Una individual solo puede tener 1 riesgo, por eso no admite agregar mas.
        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL) {
            throw new ReglaNegocioException("Solo se pueden agregar riesgos a polizas colectivas. "
                    + "Una poliza individual solo puede tener 1 riesgo");
        }
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se pueden agregar riesgos a una poliza cancelada");
        }

        Riesgo riesgo = new Riesgo();
        riesgo.setDescripcion(descripcion);
        riesgo.setEstado(EstadoRiesgo.ACTIVO);
        riesgo.setPoliza(poliza);
        riesgoRepository.save(riesgo);

        coreClient.notificar("ACTUALIZACION", polizaId);
        return riesgo;
    }

    // POST /riesgos/{id}/cancelar
    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Riesgo " + riesgoId + " no existe"));
        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new ReglaNegocioException("El riesgo ya esta cancelado");
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);

        coreClient.notificar("ACTUALIZACION", riesgo.getPolizaId());
        return riesgo;
    }

    // Metodo de apoyo: busca la poliza o lanza 404. Se reutiliza en todos los metodos.
    private Poliza buscarPoliza(Long polizaId) {
        return polizaRepository.findById(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Poliza " + polizaId + " no existe"));
    }
}
