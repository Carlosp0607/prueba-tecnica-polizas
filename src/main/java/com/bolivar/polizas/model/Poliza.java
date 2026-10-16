package com.bolivar.polizas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// @Entity: esta clase es una tabla en la base de datos (tabla "poliza")
@Entity
public class Poliza {

    // @Id: llave primaria. @GeneratedValue IDENTITY: la BD asigna el numero (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Enumerated STRING: guarda el texto "COLECTIVA" y no un numero (0, 1).
    @Enumerated(EnumType.STRING)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    private EstadoPoliza estado;

    private String tomador;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer mesesVigencia;

    // BigDecimal y no double: el dinero necesita precision exacta.
    private BigDecimal canonMensual;
    private BigDecimal prima;

    // Getters y setters: JPA los usa para cargar datos y Jackson para convertir a JSON.
    // No hay setId porque el id lo asigna la base de datos.
    public Long getId() { return id; }
    public TipoPoliza getTipo() { return tipo; }
    public void setTipo(TipoPoliza tipo) { this.tipo = tipo; }
    public EstadoPoliza getEstado() { return estado; }
    public void setEstado(EstadoPoliza estado) { this.estado = estado; }
    public String getTomador() { return tomador; }
    public void setTomador(String tomador) { this.tomador = tomador; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public Integer getMesesVigencia() { return mesesVigencia; }
    public void setMesesVigencia(Integer mesesVigencia) { this.mesesVigencia = mesesVigencia; }
    public BigDecimal getCanonMensual() { return canonMensual; }
    public void setCanonMensual(BigDecimal canonMensual) { this.canonMensual = canonMensual; }
    public BigDecimal getPrima() { return prima; }
    public void setPrima(BigDecimal prima) { this.prima = prima; }
}
