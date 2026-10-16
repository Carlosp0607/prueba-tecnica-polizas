package com.bolivar.polizas.repository;

import com.bolivar.polizas.model.EstadoPoliza;
import com.bolivar.polizas.model.Poliza;
import com.bolivar.polizas.model.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JpaRepository<Poliza, Long>: Spring genera automaticamente save, findById, findAll, delete...
// Poliza = la entidad que maneja; Long = el tipo de su id.
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    // Consulta JPQL (SQL sobre clases, no sobre tablas).
    // Los filtros son opcionales: si tipo o estado llegan null, esa condicion se ignora.
    // Asi un solo metodo sirve para: sin filtro, solo tipo, solo estado, o ambos.
    @Query("SELECT p FROM Poliza p WHERE (:tipo IS NULL OR p.tipo = :tipo) "
         + "AND (:estado IS NULL OR p.estado = :estado)")
    List<Poliza> buscar(@Param("tipo") TipoPoliza tipo, @Param("estado") EstadoPoliza estado);
}
