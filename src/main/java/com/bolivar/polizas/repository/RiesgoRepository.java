package com.bolivar.polizas.repository;

import com.bolivar.polizas.model.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {

    // Query derivada: Spring lee el NOMBRE del metodo y arma la consulta solo.
    // findBy + Poliza + Id  ->  SELECT * FROM riesgo WHERE poliza_id = ?
    List<Riesgo> findByPolizaId(Long polizaId);
}
