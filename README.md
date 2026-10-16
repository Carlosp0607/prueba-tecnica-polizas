# API de Gestión de Pólizas

Prueba técnica - Desarrollador TI (Seguros Bolívar).
Autor: Carlos Daniel Polanco Turizo

API REST en **Spring Boot 3.3 / Java 17** para gestionar pólizas de arrendamiento (individuales y colectivas) y sus riesgos.

## Arquitectura

Arquitectura en capas:

```
Cliente → ApiKeyFilter → Controller → Service → Repository → H2
                                         └→ CoreClient → /core-mock/evento
```

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Seguridad | `security` | Valida el header `x-api-key` (401 si falta o no coincide) |
| Controller | `controller` | Expone los endpoints REST, sin lógica de negocio |
| Service | `service` | Reglas de negocio, transacciones y notificación al CORE |
| Repository | `repository` | Acceso a datos con Spring Data JPA |
| Model | `model` | Entidades `Poliza` y `Riesgo` y enums de tipo y estado |
| Exception | `exception` | Manejo global de errores (404, 422) |

## Reglas de negocio

- Una póliza individual solo puede tener 1 riesgo (no admite agregar riesgos).
- No se puede renovar una póliza cancelada.
- Cancelar una póliza cancela todos sus riesgos.
- Agregar un riesgo exige validar el tipo de póliza: solo `COLECTIVA`.
- Renovar: canon y prima se incrementan con el IPC (`polizas.ipc` en `application.properties`), la vigencia se corre por el mismo número de meses y el estado pasa a `RENOVADA`.
- Toda acción que modifica estados notifica al CORE (`POST /core-mock/evento`).

## Requisitos

- Java 17 o superior
- Maven 3.9 o superior

## Ejecutar

```bash
mvn spring-boot:run
```

La API queda en `http://localhost:8080`. La base de datos H2 en memoria se carga con datos de ejemplo (`data.sql`):

| id | tipo | estado | riesgos |
|---|---|---|---|
| 1 | INDIVIDUAL | ACTIVA | 1 |
| 2 | COLECTIVA | ACTIVA | 2 |
| 3 | COLECTIVA | CANCELADA | 1 |

## Pruebas

```bash
mvn test
```

10 pruebas de integración (MockMvc) cubren endpoints, reglas de negocio, seguridad y mock del CORE.

## Endpoints

Todas las peticiones requieren el header `x-api-key: 123456`.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/polizas?tipo=&estado=` | Lista pólizas (filtros opcionales) |
| GET | `/polizas/{id}/riesgos` | Riesgos de una póliza |
| POST | `/polizas/{id}/renovar` | Renueva con IPC |
| POST | `/polizas/{id}/cancelar` | Cancela póliza y sus riesgos |
| POST | `/polizas/{id}/riesgos` | Agrega riesgo (solo colectivas) |
| POST | `/riesgos/{id}/cancelar` | Cancela un riesgo |
| POST | `/core-mock/evento` | Mock del CORE: registra el evento en logs |

### Ejemplos

```bash
curl -H "x-api-key: 123456" "http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA"

curl -H "x-api-key: 123456" http://localhost:8080/polizas/2/riesgos

curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/1/renovar

curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/2/cancelar

curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
     -d '{"descripcion":"Apartamento 201"}' http://localhost:8080/polizas/2/riesgos

curl -X POST -H "x-api-key: 123456" http://localhost:8080/riesgos/2/cancelar

curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
     -d '{"evento":"ACTUALIZACION","polizaId":555}' http://localhost:8080/core-mock/evento
```

### Códigos de respuesta

| Código | Caso |
|---|---|
| 200 / 201 | Operación exitosa |
| 401 | `x-api-key` ausente o inválida |
| 404 | Póliza o riesgo no existe |
| 422 | Regla de negocio violada |

## Decisiones y mejoras para producción

- **H2 en memoria** para que el proyecto corra sin instalar nada; en producción, Oracle o PostgreSQL.
- **API key en properties** por simplicidad; en producción, Spring Security con OAuth2/JWT y secretos en un vault.
- **Notificación al CORE síncrona y tolerante a fallos** (si falla, se registra en log y la operación no se revierte); en producción, patrón Outbox con reintentos y circuit breaker (Resilience4j).
- **CI** con GitHub Actions: compila y ejecuta las pruebas en cada push.
