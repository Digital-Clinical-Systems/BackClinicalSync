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
| Signos vitales | `POST /vital-signs` · `GET /vital-signs`, `GET /patients/{id}/vital-signs` | NURSE, PHYSICIAN · autenticado |
| Eventos clínicos | `POST /clinical-events` · `GET /clinical-events` | NURSE, PHYSICIAN · autenticado |
| Alertas | `GET /alerts`, `POST /alerts/{id}/acknowledge`, `POST /alerts/{id}/resolve` | autenticado |
| Traspasos SBAR | `POST /handovers`, `POST /handovers/{id}/acknowledge` · `GET /handovers` | NURSE · autenticado |
| Indicaciones médicas | `POST /medical-orders` · `POST /medical-orders/{id}/fulfill` · `GET /medical-orders` | PHYSICIAN · NURSE · autenticado |
| Bitácora | `GET /audit-logs` | autenticado |

## Frontend

[FrontClinicalSync](https://github.com/Digital-Clinical-Systems/FrontClinicalSync) se conecta por `/api/frontend`, que expone el mismo contrato que usaba su fake API (`/patients`, `/vitalSigns`, `/alerts`, `/medicalOrders`, `/handovers`, `/clinicalEvents`, `/auditLogs`, `/users`). Solo cambia la URL en el front:

- Vercel (`vercel.json`): `"destination": "https://<backend>/api/frontend/$1"`
- Local (`environment.development.ts`): `apiBaseUrl: 'http://localhost:8080/api/frontend'`

Estas rutas son públicas porque el front todavía no inicia sesión: el profesional viaja en el cuerpo de cada petición. Se desactivan con `FRONTEND_API_ENABLED=false`.

## Despliegue

| Variable | Ejemplo |
| :--- | :--- |
| `DB_URL` | `jdbc:mysql://host:3306/clinicalsync` |
| `DB_USERNAME`, `DB_PASSWORD` | credenciales de MySQL |
| `JWT_SECRET` | cadena aleatoria de 32 caracteres o más |
| `CORS_ALLOWED_ORIGINS` | `https://tu-front.vercel.app` |
| `PORT` | lo asigna la plataforma (por defecto 8080) |
