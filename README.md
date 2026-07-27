# Sistema de Gestión de Turnos Médicos

## Stack
- Java 21 · Spring Boot 3.2.4 · Maven
- PostgreSQL · Spring Data JPA · Hibernate
- Spring Security · JWT (jjwt 0.12.5)
- Lombok · MapStruct · Bean Validation

---

## Estructura de paquetes

```
com.clinica/
├── TurnosMedicosApplication.java
├── config/
│   └── SecurityConfig.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── util/
│   └── JwtUtil.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BusinessException.java
├── entity/
│   ├── BaseEntity.java              ← UUID + auditoría
│   ├── SoftDeleteEntity.java        ← + deleted_at
│   ├── Specialty.java
│   ├── HealthInsurance.java
│   ├── Permission.java
│   ├── Role.java
│   ├── User.java
│   ├── Patient.java
│   ├── PatientInsurance.java
│   ├── Doctor.java
│   ├── Office.java
│   ├── DoctorOffice.java
│   ├── Schedule.java
│   ├── Availability.java
│   ├── Appointment.java
│   ├── AppointmentStatusHistory.java
│   ├── ClinicalRecord.java
│   ├── Consultation.java
│   ├── Prescription.java
│   ├── PrescriptionItem.java
│   ├── MedicalStudy.java
│   ├── Payment.java
│   ├── Invoice.java
│   ├── Notification.java
│   └── SystemConfiguration.java
├── repository/
│   ├── AllRepositories.java         ← todos los JpaRepository
│   └── AppointmentStatusHistoryRepository.java
├── service/
│   ├── AuthService.java
│   ├── PatientService.java
│   ├── DoctorService.java
│   ├── ScheduleService.java
│   ├── AvailabilityService.java
│   ├── AppointmentService.java
│   ├── ClinicalServices.java        ← ClinicalRecord + Consultation + Prescription + Study
│   └── PaymentNotificationService.java
├── controller/
│   ├── AuthController.java
│   ├── PatientController.java
│   ├── DoctorController.java        ← + SpecialtyController + HealthInsuranceController
│   ├── AppointmentController.java   ← + ScheduleController + AvailabilityController
│   ├── ClinicalController.java      ← + ConsultationController + PrescriptionController + StudyController
│   └── PaymentNotificationController.java ← + NotificationController + ConfigController
└── dto/
    ├── AuthUserDtos.java
    ├── MedicalDtos.java
    └── AppointmentDtos.java
```

---

## Ejecutar

### 1. Crear la base de datos
```bash
psql -U postgres -f src/main/resources/schema.sql
```

### 2. Configurar credenciales
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.username=postgres
spring.datasource.password=tu_password
app.jwt.secret=clave_de_al_menos_32_caracteres_aqui
```

### 3. Compilar y correr
```bash
mvn spring-boot:run
```

---

## Endpoints principales

### Autenticación
```
POST /api/auth/login
Body: { "username": "admin", "password": "1234" }
→ Retorna token JWT
```

Todos los demás endpoints requieren header:
```
Authorization: Bearer <token>
```

### Turnos
```
GET    /api/turnos?doctorId=&date=2024-06-01    Ver agenda del día
POST   /api/turnos                               Solicitar turno
PATCH  /api/turnos/{id}/confirmar               Confirmar
PATCH  /api/turnos/{id}/cancelar                Cancelar
PATCH  /api/turnos/{id}/completar               Marcar como atendido
PATCH  /api/turnos/{id}/ausente                 Marcar inasistencia
GET    /api/turnos/paciente/{id}                Turnos de un paciente
```

### Disponibilidad
```
GET  /api/disponibilidad/{doctorId}/slots?date=  Slots libres del día
GET  /api/disponibilidad/{doctorId}?from=&to=    Bloqueos en rango
POST /api/disponibilidad/{doctorId}              Registrar bloqueo/vacación
```

### Pacientes
```
GET    /api/patients?name=          Buscar pacientes
GET    /api/patients/{id}           Ver paciente
POST   /api/patients               Crear paciente (crea historia clínica automáticamente)
PUT    /api/patients/{id}           Actualizar
DELETE /api/patients/{id}           Soft delete
POST   /api/patients/{id}/insurances  Agregar obra social
```

### Historia clínica
```
GET   /api/historia-clinica/paciente/{id}    Ver historia clínica
PATCH /api/historia-clinica/paciente/{id}    Actualizar antecedentes
GET   /api/consultas/paciente/{id}           Historial de consultas
POST  /api/consultas                         Registrar consulta
GET   /api/recetas/paciente/{id}             Recetas del paciente
POST  /api/recetas                           Emitir receta
GET   /api/estudios/paciente/{id}            Estudios del paciente
POST  /api/estudios                          Solicitar estudio
PATCH /api/estudios/{id}/resultado           Cargar resultado
```

### Pagos
```
POST  /api/pagos                     Registrar pago
PATCH /api/pagos/{id}/confirmar      Confirmar cobro
PATCH /api/pagos/{id}/reembolsar     Reembolso
GET   /api/pagos?status=PENDING      Pagos pendientes
```

### Notificaciones
```
GET   /api/notificaciones              Las del usuario autenticado
GET   /api/notificaciones/no-leidas    Sin leer
GET   /api/notificaciones/no-leidas/cantidad
PATCH /api/notificaciones/{id}/leer    Marcar leída
PATCH /api/notificaciones/leer-todas
```

### Agenda médica
```
GET  /api/agenda/{doctorId}           Horarios del médico
POST /api/agenda/{doctorId}           Crear horario semanal
PATCH /api/agenda/{scheduleId}/desactivar
```

### Configuración
```
GET /api/configuracion             Ver toda la configuración (ADMIN)
PUT /api/configuracion/{key}       Actualizar valor
```

---

## Roles y permisos

| Endpoint                  | ADMIN | MEDICO | RECEPCIONISTA |
|---------------------------|-------|--------|---------------|
| Crear paciente            | ✓     |        | ✓             |
| Ver paciente              | ✓     | ✓      | ✓             |
| Registrar consulta        | ✓     | ✓      |               |
| Emitir receta             |       | ✓      |               |
| Solicitar turno           | ✓     |        | ✓             |
| Confirmar/cancelar turno  | ✓     |        | ✓             |
| Registrar pago            | ✓     |        | ✓             |
| Gestionar usuarios        | ✓     |        |               |
| Configuración del sistema | ✓     |        |               |

---

## Diagrama de relaciones

```
Specialty       ──<  Doctor
HealthInsurance ──<  PatientInsurance  >── Patient
Role            ──<  User
Role            ──<  RolePermission    >── Permission
User            ──1  Doctor
Doctor          ──<  DoctorOffice      >── Office
Doctor          ──<  Schedule
Doctor          ──<  Availability
Doctor          ──<  Appointment
Patient         ──<  Appointment
Patient         ──1  ClinicalRecord
Appointment     ──1  Consultation
Appointment     ──1  Payment           ──1  Invoice
Appointment     ──<  AppointmentStatusHistory
ClinicalRecord  ──<  Consultation
Consultation    ──<  Prescription      ──<  PrescriptionItem
Consultation    ──<  MedicalStudy
ClinicalRecord  ──<  Prescription
ClinicalRecord  ──<  MedicalStudy
User            ──<  Notification
Appointment     ──<  Notification
Patient         ──<  MovimientoCuentaCorriente (cuenta_corriente)
```
