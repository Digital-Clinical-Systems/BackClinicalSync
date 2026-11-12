# ClinicalSync Platform (BackClinicalSync)

API REST de **ClinicalSync** para unidades de cuidados cardiovasculares: pacientes, signos vitales con riesgo derivado, eventos clínicos, alertas, traspasos SBAR, indicaciones médicas y bitácora de auditoría.

Curso 1ASI0729 Desarrollo de Aplicaciones Open Source, UPC.

## Stack

Java 25 · Spring Boot 4.1.2 · Maven · Spring Data JPA · MySQL · Spring Security + JWT · Jakarta Validation · springdoc OpenAPI · Lombok

## Documentación

Swagger UI: http://localhost:8080/swagger-ui/index.html

## Usuarios demo

Contraseña de todos: `ClinicalSync2026`

| Usuario | Rol |
| :--- | :--- |
| `enf-001`, `enf-002` | NURSE |
| `med-001` | PHYSICIAN |
| `coord-001` | COORDINATOR |
| `aud-001` | AUDITOR |

## Endpoints (`/api/v1`)

| Recurso | Rutas | Rol |
| :--- | :--- | :--- |
| Autenticación | `POST /authentication/sign-in`, `POST /authentication/sign-up` | público |
| Usuarios | `GET /users`, `GET /users/{id}`, `GET /roles` | autenticado |
| Pacientes | `GET/POST /patients`, `GET /patients/{id}`, `POST /patients/{id}/discharge` | autenticado |
| Signos vitales | `POST /vital-signs` · `GET /vital-signs`, `GET /patients/{id}/vital-signs` | NURSE · autenticado |
| Eventos clínicos | `POST /clinical-events` · `GET /clinical-events` | NURSE, PHYSICIAN · autenticado |
| Alertas | `GET /alerts`, `POST /alerts/{id}/acknowledge`, `POST /alerts/{id}/resolve` | autenticado |
| Traspasos SBAR | `POST /handovers`, `POST /handovers/{id}/acknowledge` · `GET /handovers` | NURSE · autenticado |
| Indicaciones médicas | `POST /medical-orders` · `POST /medical-orders/{id}/fulfill` · `GET /medical-orders` | PHYSICIAN · NURSE · autenticado |
| Bitácora | `GET /audit-logs` | COORDINATOR, AUDITOR |
