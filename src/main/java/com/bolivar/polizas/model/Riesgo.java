package com.bolivar.polizas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

// Riesgo = inmueble asegurado. Tabla "riesgo".
@Entity
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoRiesgo estado;

    // MUCHOS riesgos pertenecen a UNA poliza. Crea la columna "poliza_id" (llave foranea).
    // @JsonIgnore: el JSON no incluye la poliza completa, solo su id (ver getPolizaId).
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    public Long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstadoRiesgo getEstado() { return estado; }
    public void setEstado(EstadoRiesgo estado) { this.estado = estado; }
    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }

    // En el JSON se muestra solo el id de la poliza: "polizaId": 2
    public Long getPolizaId() { return poliza.getId(); }
}
