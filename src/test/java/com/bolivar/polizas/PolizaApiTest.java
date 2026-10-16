package com.bolivar.polizas;

import com.bolivar.polizas.service.CoreClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de integracion: levantan la app completa (con H2 y data.sql)
// y simulan peticiones HTTP con MockMvc, sin abrir un puerto real.
// @DirtiesContext: reinicia la app despues de cada prueba para que los datos
// vuelvan a su estado inicial y una prueba no afecte a otra.
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PolizaApiTest {

    private static final String API_KEY = "123456";

    // MockMvc: simula un cliente HTTP (como Postman, pero desde el codigo)
    @Autowired
    private MockMvc mvc;

    // @MockBean: reemplaza el CoreClient real por uno falso.
    // Asi las pruebas no dependen de la red y se puede verificar que se llamo.
    @MockBean
    private CoreClient coreClient;

    // Seguridad: sin header x-api-key -> 401
    @Test
    void sinApiKeyResponde401() throws Exception {
        mvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized());
    }

    // GET /polizas con filtros: solo la poliza 2 es COLECTIVA y ACTIVA
    @Test
    void listaPolizasPorTipoYEstado() throws Exception {
        mvc.perform(get("/polizas?tipo=COLECTIVA&estado=ACTIVA").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    // Renovar: canon 1.500.000 + 5.2% = 1.578.000; prima = 1.578.000 x 12 = 18.936.000
    @Test
    void renovarAplicaIpcYCambiaEstado() throws Exception {
        mvc.perform(post("/polizas/1/renovar").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"))
                .andExpect(jsonPath("$.canonMensual").value(1578000.00))
                .andExpect(jsonPath("$.prima").value(18936000.00));

        // Verifica que se notifico al CORE
        verify(coreClient).notificar("ACTUALIZACION", 1L);
    }

    // Regla: no se puede renovar una poliza cancelada -> 422
    @Test
    void noSePuedeRenovarPolizaCancelada() throws Exception {
        mvc.perform(post("/polizas/3/renovar").header("x-api-key", API_KEY))
                .andExpect(status().isUnprocessableEntity());
    }

    // Regla: cancelar la poliza cancela todos sus riesgos
    @Test
    void cancelarPolizaCancelaSusRiesgos() throws Exception {
        mvc.perform(post("/polizas/2/cancelar").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        mvc.perform(get("/polizas/2/riesgos").header("x-api-key", API_KEY))
                .andExpect(jsonPath("$[0].estado").value("CANCELADO"))
                .andExpect(jsonPath("$[1].estado").value("CANCELADO"));
    }

    // Agregar riesgo a colectiva -> 201
    @Test
    void agregarRiesgoAPolizaColectiva() throws Exception {
        mvc.perform(post("/polizas/2/riesgos").header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Apartamento 201\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.polizaId").value(2));
    }

    // Regla: individual solo 1 riesgo / validacion de tipo -> 422
    @Test
    void noSePuedeAgregarRiesgoAPolizaIndividual() throws Exception {
        mvc.perform(post("/polizas/1/riesgos").header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Otro inmueble\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    // Cancelar un riesgo puntual
    @Test
    void cancelarRiesgo() throws Exception {
        mvc.perform(post("/riesgos/2/cancelar").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    // Poliza inexistente -> 404
    @Test
    void polizaInexistenteResponde404() throws Exception {
        mvc.perform(get("/polizas/99/riesgos").header("x-api-key", API_KEY))
                .andExpect(status().isNotFound());
    }

    // Mock del CORE responde y registra
    @Test
    void coreMockRegistraEvento() throws Exception {
        mvc.perform(post("/core-mock/evento").header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"evento\":\"ACTUALIZACION\",\"polizaId\":555}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("REGISTRADO"));
    }
}
