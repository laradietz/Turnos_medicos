-- ============================================================
--  Migración: convertir columnas de tipo ENUM nativo de Postgres
--  a VARCHAR simple, para que coincidan con el mapeo de Hibernate
--  (@Enumerated(EnumType.STRING) espera un VARCHAR, no un enum nativo).
--
--  Sin este cambio, crear/editar pacientes, turnos, médicos,
--  estudios, pagos, facturas o notificaciones falla con un
--  error 500 al intentar el INSERT/UPDATE.
--
--  Ejecutar UNA VEZ contra la base turnos_medicos ya existente.
-- ============================================================

ALTER TABLE patients
    ALTER COLUMN gender     TYPE VARCHAR(10) USING gender::text,
    ALTER COLUMN blood_type TYPE VARCHAR(10) USING blood_type::text;

ALTER TABLE schedules
    ALTER COLUMN day_of_week TYPE VARCHAR(10) USING day_of_week::text;

ALTER TABLE availabilities
    ALTER COLUMN type TYPE VARCHAR(20) USING type::text;

ALTER TABLE appointments
    ALTER COLUMN status TYPE VARCHAR(20) USING status::text,
    ALTER COLUMN type   TYPE VARCHAR(20) USING type::text;

ALTER TABLE appointment_status_histories
    ALTER COLUMN previous_status TYPE VARCHAR(20) USING previous_status::text,
    ALTER COLUMN new_status      TYPE VARCHAR(20) USING new_status::text;

ALTER TABLE medical_studies
    ALTER COLUMN status TYPE VARCHAR(20) USING status::text;

ALTER TABLE payments
    ALTER COLUMN status         TYPE VARCHAR(20) USING status::text,
    ALTER COLUMN payment_method TYPE VARCHAR(20) USING payment_method::text;

ALTER TABLE invoices
    ALTER COLUMN invoice_type TYPE VARCHAR(5) USING invoice_type::text;

ALTER TABLE notifications
    ALTER COLUMN type    TYPE VARCHAR(40) USING type::text,
    ALTER COLUMN channel TYPE VARCHAR(10) USING channel::text;

-- Verificación: no debería quedar ninguna columna con tipo *_enum
SELECT table_name, column_name, udt_name
FROM information_schema.columns
WHERE udt_name LIKE '%_enum'
ORDER BY table_name, column_name;
