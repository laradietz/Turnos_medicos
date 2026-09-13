# Sistema de Gestión de Turnos Médicos

API REST para la gestión integral de una clínica: turnos, agenda médica,
pacientes, historia clínica, consultas, recetas, estudios y pagos, con
autenticación JWT y permisos diferenciados por rol (ADMIN, MÉDICO,
RECEPCIONISTA).

## ¿Qué problema resuelve?

Una clínica necesita coordinar la disponibilidad de varios médicos, evitar
turnos superpuestos, mantener la historia clínica de cada paciente
actualizada y llevar el registro de pagos — todo con reglas de acceso
distintas según quién esté usando el sistema (no todos ven ni pueden hacer
lo mismo). Este proyecto modela ese dominio completo como una API REST con
autorización verificada del lado del servidor en cada endpoint.

## Características

- Autenticación con JWT (login, roles y permisos)
- Gestión de pacientes, con historia clínica creada automáticamente al alta
- Agenda médica: horarios semanales, bloqueos/vacaciones y cálculo de slots libres
- Turnos: solicitar, confirmar, cancelar, completar o marcar inasistencia
- Historia clínica: consultas, recetas y estudios médicos con resultados
- Pagos y facturación asociados a cada turno
- Notificaciones por usuario
- Baja lógica (soft delete) en las entidades sensibles, con auditoría de fechas

## Arquitectura

```
Cliente HTTP
    ↓
Controller       (valida entrada, no contiene lógica de negocio)
    ↓
Service          (reglas de negocio, transacciones)
    ↓
Repository       (Spring Data JPA)
    ↓
PostgreSQL
```

Capas separadas por responsabilidad, un archivo por clase:

```
com.clinica/
├── config/        Seguridad (Spring Security)
├── security/      Filtro JWT, UserDetailsService
├── util/          Utilidades (generación/validación de JWT)
├── exception/     GlobalExceptionHandler + excepciones de dominio
├── entity/        Entidades JPA (BaseEntity con UUID + auditoría; SoftDeleteEntity)
├── repository/    Un JpaRepository por entidad
├── service/       Lógica de negocio, un service por dominio
├── mapper/        Entidad ↔ DTO (MapStruct)
├── dto/           Records de entrada/salida por dominio
└── controller/    Un controller por recurso
```

## Tecnologías

- Java 21 · Spring Boot 3.2 · Maven
- Spring Data JPA · Hibernate · PostgreSQL
- Spring Security + JWT (jjwt)
- MapStruct (mapeo entidad ↔ DTO) · Lombok · Bean Validation
- Docker / Docker Compose

## Instalación y ejecución

### Con Docker (recomendado)

```bash
git clone https://github.com/laradietz/Turnos_medicos.git
cd Turnos_medicos
docker compose up --build
```

Esto levanta Postgres (con el esquema y un usuario admin de ejemplo ya
cargados) y la API. Queda disponible en `http://localhost:8081`.

### Local, con tu propio PostgreSQL

Requiere Java 21 y Maven.

```bash
psql -U postgres -f src/main/resources/schema.sql
psql -U postgres -d turnos_medicos -f src/main/resources/seed-admin.sql   # opcional, datos de ejemplo
mvn spring-boot:run
```

## Configuración

Copiá `.env.example` a `.env` y ajustá los valores si tu PostgreSQL local
no usa `postgres`/`postgres`. En desarrollo local (con Docker o con los
defaults de `application.properties`) no hace falta tocar nada.

## Uso

```
POST /api/auth/login
Body: { "username": "admin", "password": "admin123" }
→ Retorna un token JWT
```

Todos los demás endpoints requieren el header `Authorization: Bearer <token>`.

Endpoints principales (ver el código de cada controller para el detalle
completo de parámetros):

| Recurso | Endpoints |
|---|---|
| Turnos | `GET/POST /api/turnos`, `PATCH /api/turnos/{id}/confirmar\|cancelar\|completar\|ausente` |
| Disponibilidad | `GET /api/disponibilidad/{doctorId}/slots`, `POST /api/disponibilidad/{doctorId}` |
| Pacientes | `GET/POST/PUT/DELETE /api/patients`, `POST /api/patients/{id}/insurances` |
| Historia clínica | `GET/PATCH /api/historia-clinica/paciente/{id}`, consultas/recetas/estudios asociados |
| Pagos | `POST /api/pagos`, `PATCH /api/pagos/{id}/confirmar\|reembolsar` |
| Notificaciones | `GET /api/notificaciones`, `PATCH /api/notificaciones/{id}/leer` |
| Agenda | `GET/POST /api/agenda/{doctorId}` |

### Roles y permisos

| Acción | ADMIN | MÉDICO | RECEPCIONISTA |
|---|:---:|:---:|:---:|
| Crear paciente | ✓ | | ✓ |
| Ver paciente | ✓ | ✓ | ✓ |
| Registrar consulta | ✓ | ✓ | |
| Emitir receta | | ✓ | |
| Solicitar/confirmar/cancelar turno | ✓ | | ✓ |
| Registrar pago | ✓ | | ✓ |
| Gestionar usuarios | ✓ | | |
| Configuración del sistema | ✓ | | |

## Qué aprendí

- Modelar un dominio real con relaciones de varios niveles (paciente → historia
  clínica → consulta → receta/estudio) sin que el grafo de entidades se vuelva
  inmanejable.
- Separar autorización por rol a nivel de servicio en vez de solo ocultar
  botones en un hipotético frontend — cada operación sensible verifica el rol
  del lado del servidor.
- Usar baja lógica (soft delete) con auditoría en vez de `DELETE` físico para
  datos clínicos, donde perder el historial no es una opción.
- Adaptar un script SQL pensado para instalación manual (con su propio
  `CREATE DATABASE` y locale) para que también funcione como script de
  inicialización de un contenedor Docker.

## Próximas mejoras

- Tests de integración sobre los endpoints críticos (turnos, disponibilidad).
- Reemplazar el script `schema.sql` manual por migraciones versionadas con Flyway.
