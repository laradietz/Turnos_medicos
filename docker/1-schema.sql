-- ============================================================
--  Sistema de Gestión de Turnos Médicos
--  PostgreSQL — 3FN — UUID — Auditoría — Soft Delete
-- ============================================================


-- Extensión para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── ENUMERACIONES ────────────────────────────────────────────

CREATE TYPE gender_enum              AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE blood_type_enum          AS ENUM ('A_POS','A_NEG','B_POS','B_NEG','AB_POS','AB_NEG','O_POS','O_NEG');
CREATE TYPE role_name_enum           AS ENUM ('ADMIN', 'MEDICO', 'RECEPCIONISTA');
CREATE TYPE appointment_status_enum  AS ENUM ('PENDING','CONFIRMED','CANCELLED','COMPLETED','NO_SHOW','RESCHEDULED');
CREATE TYPE appointment_type_enum    AS ENUM ('FIRST_VISIT','FOLLOW_UP','EMERGENCY','CHECKUP');
CREATE TYPE availability_type_enum   AS ENUM ('AVAILABLE','BLOCKED','VACATION','HOLIDAY');
CREATE TYPE study_status_enum        AS ENUM ('ORDERED','PENDING','COMPLETED','CANCELLED');
CREATE TYPE payment_status_enum      AS ENUM ('PENDING','PAID','PARTIALLY_PAID','REFUNDED','CANCELLED');
CREATE TYPE payment_method_enum      AS ENUM ('CASH','CREDIT_CARD','DEBIT_CARD','TRANSFER','INSURANCE');
CREATE TYPE invoice_type_enum        AS ENUM ('A','B','C');
CREATE TYPE notification_type_enum   AS ENUM ('APPOINTMENT_REMINDER','APPOINTMENT_CONFIRMED','APPOINTMENT_CANCELLED','APPOINTMENT_RESCHEDULED','STUDY_READY','PRESCRIPTION_READY','GENERAL');
CREATE TYPE notification_channel_enum AS ENUM ('EMAIL','SMS','PUSH','IN_APP');
CREATE TYPE day_of_week_enum         AS ENUM ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY');
CREATE TYPE study_status_type_enum   AS ENUM ('ORDERED','PENDING','COMPLETED','CANCELLED');

-- ── TABLA BASE (columnas de auditoría) aplicadas a todas ─────
-- Se repite en cada tabla para mayor claridad y compatibilidad.

-- ── PERMISSIONS ──────────────────────────────────────────────
CREATE TABLE permissions (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(80)   NOT NULL,
    description VARCHAR(150),
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_permission_code UNIQUE (code)
);

-- ── ROLES ────────────────────────────────────────────────────
CREATE TABLE roles (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(50)   NOT NULL,
    description VARCHAR(150),
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_role_name UNIQUE (name)
);

-- ── ROLE_PERMISSIONS (N:M) ───────────────────────────────────
CREATE TABLE role_permissions (
    role_id       UUID NOT NULL REFERENCES roles(id)       ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- ── USERS ────────────────────────────────────────────────────
CREATE TABLE users (
    id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    username       VARCHAR(80)   NOT NULL,
    email          VARCHAR(150)  NOT NULL,
    password_hash  VARCHAR(255)  NOT NULL,
    first_name     VARCHAR(100)  NOT NULL,
    last_name      VARCHAR(100)  NOT NULL,
    phone          VARCHAR(30),
    active         BOOLEAN       NOT NULL DEFAULT TRUE,
    role_id        UUID          NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP,

    CONSTRAINT uk_user_username UNIQUE (username),
    CONSTRAINT uk_user_email    UNIQUE (email)
);

-- ── SPECIALTIES ──────────────────────────────────────────────
CREATE TABLE specialties (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100)  NOT NULL,
    description VARCHAR(255),
    active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,

    CONSTRAINT uk_specialty_name UNIQUE (name)
);

-- ── HEALTH_INSURANCES ────────────────────────────────────────
CREATE TABLE health_insurances (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150)  NOT NULL,
    code        VARCHAR(50),
    address     VARCHAR(255),
    phone       VARCHAR(30),
    active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,

    CONSTRAINT uk_health_insurance_name UNIQUE (name)
);

-- ── PATIENTS ─────────────────────────────────────────────────
CREATE TABLE patients (
    id                       UUID               PRIMARY KEY DEFAULT gen_random_uuid(),
    dni                      VARCHAR(20)        NOT NULL,
    first_name               VARCHAR(100)       NOT NULL,
    last_name                VARCHAR(100)       NOT NULL,
    birth_date               DATE               NOT NULL,
    gender                   VARCHAR(10),
    email                    VARCHAR(150),
    phone                    VARCHAR(30),
    address                  VARCHAR(200),
    city                     VARCHAR(100),
    postal_code              VARCHAR(50),
    blood_type               VARCHAR(10),
    emergency_contact_name   VARCHAR(150),
    emergency_contact_phone  VARCHAR(30),
    active                   BOOLEAN            NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP          NOT NULL DEFAULT NOW(),
    deleted_at               TIMESTAMP,

    CONSTRAINT uk_patient_dni UNIQUE (dni),
    CONSTRAINT chk_patient_birth_date CHECK (birth_date <= CURRENT_DATE)
);

-- ── PATIENT_INSURANCES ───────────────────────────────────────
CREATE TABLE patient_insurances (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    affiliate_number    VARCHAR(50)   NOT NULL,
    plan                VARCHAR(80),
    expiration_date     DATE,
    is_primary          BOOLEAN       NOT NULL DEFAULT FALSE,
    patient_id          UUID          NOT NULL REFERENCES patients(id)          ON DELETE CASCADE,
    health_insurance_id UUID          NOT NULL REFERENCES health_insurances(id) ON DELETE RESTRICT,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_patient_insurance UNIQUE (patient_id, health_insurance_id)
);

-- ── DOCTORS ──────────────────────────────────────────────────
CREATE TABLE doctors (
    id                        UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    license_number            VARCHAR(30)   NOT NULL,
    first_name                VARCHAR(100)  NOT NULL,
    last_name                 VARCHAR(100)  NOT NULL,
    email                     VARCHAR(150),
    phone                     VARCHAR(30),
    biography                 VARCHAR(255),
    consultation_fee_minutes  INTEGER       NOT NULL DEFAULT 30
                              CONSTRAINT chk_doctor_fee_minutes CHECK (consultation_fee_minutes > 0),
    active                    BOOLEAN       NOT NULL DEFAULT TRUE,
    user_id                   UUID          REFERENCES users(id)       ON DELETE SET NULL,
    specialty_id              UUID          NOT NULL REFERENCES specialties(id) ON DELETE RESTRICT,
    created_at                TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at                TIMESTAMP,

    CONSTRAINT uk_doctor_license UNIQUE (license_number),
    CONSTRAINT uk_doctor_user    UNIQUE (user_id)
);

-- ── OFFICES ──────────────────────────────────────────────────
CREATE TABLE offices (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    number      VARCHAR(20)   NOT NULL,
    description VARCHAR(100),
    floor       VARCHAR(100),
    active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,

    CONSTRAINT uk_office_number UNIQUE (number)
);

-- ── DOCTOR_OFFICES ───────────────────────────────────────────
CREATE TABLE doctor_offices (
    id          UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    valid_from  DATE    NOT NULL,
    valid_until DATE,
    doctor_id   UUID    NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    office_id   UUID    NOT NULL REFERENCES offices(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_doctor_office      UNIQUE (doctor_id, office_id, valid_from),
    CONSTRAINT chk_doctor_office_dates CHECK (valid_until IS NULL OR valid_until > valid_from)
);

-- ── SCHEDULES ────────────────────────────────────────────────
CREATE TABLE schedules (
    id                    UUID              PRIMARY KEY DEFAULT gen_random_uuid(),
    day_of_week           VARCHAR(10)  NOT NULL,
    start_time            TIME              NOT NULL,
    end_time              TIME              NOT NULL,
    slot_duration_minutes INTEGER           NOT NULL DEFAULT 30
                          CONSTRAINT chk_slot_duration CHECK (slot_duration_minutes > 0),
    valid_from            DATE              NOT NULL,
    valid_until           DATE,
    active                BOOLEAN           NOT NULL DEFAULT TRUE,
    doctor_id             UUID              NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    office_id             UUID              REFERENCES offices(id)          ON DELETE SET NULL,
    created_at            TIMESTAMP         NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP         NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_schedule_doctor_day UNIQUE (doctor_id, day_of_week, valid_from),
    CONSTRAINT chk_schedule_times     CHECK (end_time > start_time),
    CONSTRAINT chk_schedule_dates     CHECK (valid_until IS NULL OR valid_until > valid_from)
);

-- ── AVAILABILITIES ───────────────────────────────────────────
CREATE TABLE availabilities (
    id          UUID                    PRIMARY KEY DEFAULT gen_random_uuid(),
    date        DATE                    NOT NULL,
    start_time  TIME,
    end_time    TIME,
    type        VARCHAR(20)  NOT NULL,
    reason      VARCHAR(255),
    doctor_id   UUID                    NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    created_at  TIMESTAMP               NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP               NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_availability_times CHECK (end_time IS NULL OR end_time > start_time)
);

-- ── APPOINTMENTS ─────────────────────────────────────────────
CREATE TABLE appointments (
    id                  UUID                      PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_date    DATE                      NOT NULL,
    start_time          TIME                      NOT NULL,
    end_time            TIME                      NOT NULL,
    status              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    type                VARCHAR(20)     NOT NULL DEFAULT 'FIRST_VISIT',
    notes               VARCHAR(500),
    cancellation_reason VARCHAR(255),
    reminder_sent       BOOLEAN                   NOT NULL DEFAULT FALSE,
    patient_id          UUID                      NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
    doctor_id           UUID                      NOT NULL REFERENCES doctors(id)  ON DELETE RESTRICT,
    office_id           UUID                      REFERENCES offices(id)            ON DELETE SET NULL,
    health_insurance_id UUID                      REFERENCES health_insurances(id)  ON DELETE SET NULL,
    created_at          TIMESTAMP                 NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP                 NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP,

    CONSTRAINT chk_appointment_times CHECK (end_time > start_time)
);

-- ── APPOINTMENT_STATUS_HISTORIES ─────────────────────────────
CREATE TABLE appointment_status_histories (
    id              UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    previous_status VARCHAR(20),
    new_status      VARCHAR(20)  NOT NULL,
    reason          VARCHAR(255),
    changed_by      VARCHAR(100),
    appointment_id  UUID                     NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    created_at      TIMESTAMP                NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP                NOT NULL DEFAULT NOW()
);

-- ── CLINICAL_RECORDS ─────────────────────────────────────────
CREATE TABLE clinical_records (
    id                   UUID   PRIMARY KEY DEFAULT gen_random_uuid(),
    allergies            TEXT,
    chronic_conditions   TEXT,
    current_medications  TEXT,
    family_history       TEXT,
    surgical_history     TEXT,
    notes                TEXT,
    patient_id           UUID   NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_clinical_record_patient UNIQUE (patient_id)
);

-- ── CONSULTATIONS ────────────────────────────────────────────
CREATE TABLE consultations (
    id                 UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    reason             TEXT,
    symptoms           TEXT,
    diagnosis          TEXT,
    treatment          TEXT,
    observations       TEXT,
    weight_kg          NUMERIC(5,2),
    height_cm          NUMERIC(5,1),
    blood_pressure     VARCHAR(20),
    temperature        NUMERIC(4,1),
    heart_rate         INTEGER        CONSTRAINT chk_heart_rate CHECK (heart_rate > 0),
    appointment_id     UUID           NOT NULL REFERENCES appointments(id)     ON DELETE RESTRICT,
    clinical_record_id UUID           NOT NULL REFERENCES clinical_records(id) ON DELETE RESTRICT,
    created_at         TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_consultation_appointment UNIQUE (appointment_id)
);

-- ── PRESCRIPTIONS ────────────────────────────────────────────
CREATE TABLE prescriptions (
    id                   UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    issue_date           DATE    NOT NULL,
    expiration_date      DATE,
    notes                TEXT,
    prescription_number  VARCHAR(50),
    consultation_id      UUID    NOT NULL REFERENCES consultations(id)     ON DELETE RESTRICT,
    clinical_record_id   UUID    NOT NULL REFERENCES clinical_records(id)  ON DELETE RESTRICT,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_prescription_dates CHECK (expiration_date IS NULL OR expiration_date >= issue_date)
);

-- ── PRESCRIPTION_ITEMS ───────────────────────────────────────
CREATE TABLE prescription_items (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    medication_name  VARCHAR(200)  NOT NULL,
    dosage           VARCHAR(100)  NOT NULL,
    frequency        VARCHAR(100)  NOT NULL,
    duration         VARCHAR(100),
    instructions     VARCHAR(255),
    quantity         INTEGER       NOT NULL CONSTRAINT chk_item_quantity CHECK (quantity > 0),
    prescription_id  UUID          NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ── MEDICAL_STUDIES ──────────────────────────────────────────
CREATE TABLE medical_studies (
    id                 UUID               PRIMARY KEY DEFAULT gen_random_uuid(),
    name               VARCHAR(200)       NOT NULL,
    study_type         VARCHAR(100),
    ordered_date       DATE               NOT NULL,
    result_date        DATE,
    status             VARCHAR(20)  NOT NULL DEFAULT 'ORDERED',
    result             TEXT,
    file_url           VARCHAR(500),
    notes              VARCHAR(255),
    consultation_id    UUID               NOT NULL REFERENCES consultations(id)    ON DELETE RESTRICT,
    clinical_record_id UUID               NOT NULL REFERENCES clinical_records(id) ON DELETE RESTRICT,
    created_at         TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP          NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_study_dates CHECK (result_date IS NULL OR result_date >= ordered_date)
);

-- ── PAYMENTS ─────────────────────────────────────────────────
CREATE TABLE payments (
    id                    UUID                  PRIMARY KEY DEFAULT gen_random_uuid(),
    total_amount          NUMERIC(10,2)         NOT NULL CONSTRAINT chk_total_amount  CHECK (total_amount >= 0),
    insurance_amount      NUMERIC(10,2)                   CONSTRAINT chk_ins_amount   CHECK (insurance_amount >= 0),
    patient_amount        NUMERIC(10,2)                   CONSTRAINT chk_pat_amount   CHECK (patient_amount >= 0),
    status                VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    payment_method        VARCHAR(20),
    paid_at               TIMESTAMP,
    transaction_reference VARCHAR(100),
    notes                 VARCHAR(255),
    appointment_id        UUID                  NOT NULL REFERENCES appointments(id) ON DELETE RESTRICT,
    created_at            TIMESTAMP             NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP             NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_payment_appointment UNIQUE (appointment_id)
);

-- ── INVOICES ─────────────────────────────────────────────────
CREATE TABLE invoices (
    id              UUID               PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number  VARCHAR(30)        NOT NULL,
    invoice_type    VARCHAR(5)  NOT NULL,
    issue_date      DATE               NOT NULL,
    net_amount      NUMERIC(10,2)      NOT NULL CONSTRAINT chk_net_amount   CHECK (net_amount >= 0),
    tax_amount      NUMERIC(10,2)               CONSTRAINT chk_tax_amount   CHECK (tax_amount >= 0),
    total_amount    NUMERIC(10,2)      NOT NULL CONSTRAINT chk_inv_total    CHECK (total_amount >= 0),
    cae             VARCHAR(20),
    cae_expiration  DATE,
    pdf_url         VARCHAR(500),
    payment_id      UUID               NOT NULL REFERENCES payments(id) ON DELETE RESTRICT,
    created_at      TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP          NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_invoice_number  UNIQUE (invoice_number),
    CONSTRAINT uk_invoice_payment UNIQUE (payment_id)
);

-- ── NOTIFICATIONS ────────────────────────────────────────────
CREATE TABLE notifications (
    id             UUID                       PRIMARY KEY DEFAULT gen_random_uuid(),
    type           VARCHAR(40)     NOT NULL,
    channel        VARCHAR(10)  NOT NULL,
    subject        VARCHAR(200)               NOT NULL,
    body           TEXT                       NOT NULL,
    sent_at        TIMESTAMP,
    is_read        BOOLEAN                    NOT NULL DEFAULT FALSE,
    read_at        TIMESTAMP,
    error_message  VARCHAR(500),
    user_id        UUID                       NOT NULL REFERENCES users(id)        ON DELETE CASCADE,
    appointment_id UUID                                REFERENCES appointments(id) ON DELETE SET NULL,
    created_at     TIMESTAMP                  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP                  NOT NULL DEFAULT NOW()
);

-- ── SYSTEM_CONFIGURATIONS ────────────────────────────────────
CREATE TABLE system_configurations (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key   VARCHAR(100)  NOT NULL,
    config_value TEXT,
    description  VARCHAR(255),
    editable     BOOLEAN       NOT NULL DEFAULT TRUE,
    data_type    VARCHAR(30),
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_config_key UNIQUE (config_key)
);

-- ════════════════════════════════════════════════════════════
--  ÍNDICES
-- ════════════════════════════════════════════════════════════

-- patients
CREATE INDEX idx_patient_dni        ON patients(dni);
CREATE INDEX idx_patient_last_name  ON patients(last_name);
CREATE INDEX idx_patient_active     ON patients(active) WHERE deleted_at IS NULL;

-- doctors
CREATE INDEX idx_doctor_specialty   ON doctors(specialty_id);
CREATE INDEX idx_doctor_active      ON doctors(active) WHERE deleted_at IS NULL;
CREATE INDEX idx_doctor_last_name   ON doctors(last_name);

-- appointments (los más consultados)
CREATE INDEX idx_appt_doctor_date   ON appointments(doctor_id, appointment_date);
CREATE INDEX idx_appt_patient       ON appointments(patient_id);
CREATE INDEX idx_appt_date          ON appointments(appointment_date);
CREATE INDEX idx_appt_status        ON appointments(status);
CREATE INDEX idx_appt_active        ON appointments(appointment_date, status)
                                    WHERE deleted_at IS NULL;

-- schedules
CREATE INDEX idx_schedule_doctor    ON schedules(doctor_id);
CREATE INDEX idx_schedule_active    ON schedules(active, day_of_week);

-- availabilities
CREATE INDEX idx_avail_doctor_date  ON availabilities(doctor_id, date);

-- notifications
CREATE INDEX idx_notif_user         ON notifications(user_id);
CREATE INDEX idx_notif_unread       ON notifications(user_id, is_read) WHERE is_read = FALSE;
CREATE INDEX idx_notif_appointment  ON notifications(appointment_id);

-- clinical_records
CREATE INDEX idx_clinical_patient   ON clinical_records(patient_id);

-- prescriptions
CREATE INDEX idx_prescription_cons  ON prescriptions(consultation_id);
CREATE INDEX idx_prescription_cr    ON prescriptions(clinical_record_id);

-- medical_studies
CREATE INDEX idx_study_consultation ON medical_studies(consultation_id);
CREATE INDEX idx_study_status       ON medical_studies(status);

-- payments
CREATE INDEX idx_payment_status     ON payments(status);

-- invoices
CREATE INDEX idx_invoice_date       ON invoices(issue_date);

-- users
CREATE INDEX idx_user_role          ON users(role_id) WHERE deleted_at IS NULL;

-- ════════════════════════════════════════════════════════════
--  FUNCIÓN updated_at automático
-- ════════════════════════════════════════════════════════════

CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar trigger a todas las tablas con updated_at
DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN SELECT unnest(ARRAY[
        'permissions','roles','users','specialties','health_insurances',
        'patients','patient_insurances','doctors','offices','doctor_offices',
        'schedules','availabilities','appointments','appointment_status_histories',
        'clinical_records','consultations','prescriptions','prescription_items',
        'medical_studies','payments','invoices','notifications','system_configurations'
    ]) LOOP
        EXECUTE format('
            CREATE TRIGGER trg_%s_updated_at
            BEFORE UPDATE ON %s
            FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
        ', t, t);
    END LOOP;
END $$;

-- ════════════════════════════════════════════════════════════
--  DATOS INICIALES
-- ════════════════════════════════════════════════════════════

INSERT INTO permissions (code, description) VALUES
    ('PATIENT_READ',       'Ver pacientes'),
    ('PATIENT_WRITE',      'Crear y editar pacientes'),
    ('PATIENT_DELETE',     'Eliminar pacientes'),
    ('DOCTOR_READ',        'Ver médicos'),
    ('DOCTOR_WRITE',       'Crear y editar médicos'),
    ('APPOINTMENT_READ',   'Ver turnos'),
    ('APPOINTMENT_WRITE',  'Crear y editar turnos'),
    ('APPOINTMENT_CANCEL', 'Cancelar turnos'),
    ('CLINICAL_READ',      'Ver historia clínica'),
    ('CLINICAL_WRITE',     'Escribir en historia clínica'),
    ('PAYMENT_READ',       'Ver pagos'),
    ('PAYMENT_WRITE',      'Registrar pagos'),
    ('INVOICE_READ',       'Ver facturas'),
    ('REPORT_READ',        'Ver reportes'),
    ('CONFIG_WRITE',       'Modificar configuración'),
    ('USER_MANAGE',        'Gestionar usuarios');

INSERT INTO roles (name, description) VALUES
    ('ADMIN',          'Administrador del sistema'),
    ('MEDICO',         'Médico de la clínica'),
    ('RECEPCIONISTA',  'Recepcionista / secretaria');

-- ADMIN: todos los permisos
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN';

-- MEDICO: permisos clínicos
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'MEDICO'
  AND p.code IN ('PATIENT_READ','APPOINTMENT_READ','APPOINTMENT_WRITE',
                  'CLINICAL_READ','CLINICAL_WRITE','REPORT_READ');

-- RECEPCIONISTA: gestión de turnos y pacientes
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'RECEPCIONISTA'
  AND p.code IN ('PATIENT_READ','PATIENT_WRITE','APPOINTMENT_READ',
                  'APPOINTMENT_WRITE','APPOINTMENT_CANCEL',
                  'PAYMENT_READ','PAYMENT_WRITE','INVOICE_READ');

INSERT INTO system_configurations (config_key, config_value, description, editable, data_type) VALUES
    ('clinic_name',              'Mi Clínica',    'Nombre de la clínica',                   TRUE,  'STRING'),
    ('clinic_address',           '',               'Dirección de la clínica',                TRUE,  'STRING'),
    ('clinic_phone',             '',               'Teléfono de la clínica',                 TRUE,  'STRING'),
    ('default_slot_minutes',     '30',             'Duración por defecto del turno (min)',   TRUE,  'INTEGER'),
    ('reminder_hours_before',    '24',             'Horas antes del turno para recordatorio',TRUE,  'INTEGER'),
    ('max_appointments_per_day', '20',             'Máx. turnos diarios por médico',         TRUE,  'INTEGER'),
    ('invoice_prefix',           'FC',             'Prefijo de número de factura',           TRUE,  'STRING'),
    ('timezone',                 'America/Argentina/Buenos_Aires', 'Zona horaria del sistema', FALSE, 'STRING');
