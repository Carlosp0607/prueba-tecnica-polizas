package com.bolivar.polizas.repository;

import com.bolivar.polizas.model.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {

    // Consulta explicita: trae los riesgos cuya poliza tenga ese id.
    // Se escribe a mano (y no como query derivada por nombre) porque Riesgo
    // tiene un getter getPolizaId() para el JSON, y Spring lo confundiria con un atributo.
    @Query("SELECT r FROM Riesgo r WHERE r.poliza.id = :polizaId")
    List<Riesgo> findByPolizaId(@Param("polizaId") Long polizaId);
}
