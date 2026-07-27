-- ============================================================
--  Datos semilla: usuario administrador de prueba
--  Ejecutar DESPUÉS de schema.sql, contra la base turnos_medicos
-- ============================================================

INSERT INTO users (username, email, password_hash, first_name, last_name, phone, active, role_id)
SELECT
    'admin',
    'admin@clinica.com',
    '$2b$10$ufXnRxXciGHWmER4WK4Bt.1kil8mnYU8dSAiqZajlN9A9JcTHhDGq', -- contraseña: admin123
    'Administrador',
    'Sistema',
    NULL,
    TRUE,
    r.id
FROM roles r
WHERE r.name = 'ADMIN';

-- Usuario para probar como recepcionista (opcional)
INSERT INTO users (username, email, password_hash, first_name, last_name, phone, active, role_id)
SELECT
    'recepcion',
    'recepcion@clinica.com',
    '$2b$10$ufXnRxXciGHWmER4WK4Bt.1kil8mnYU8dSAiqZajlN9A9JcTHhDGq', -- contraseña: admin123
    'Recepción',
    'Clínica',
    NULL,
    TRUE,
    r.id
FROM roles r
WHERE r.name = 'RECEPCIONISTA';

-- Verificación
SELECT username, email, first_name, last_name, active,
       (SELECT name FROM roles WHERE id = users.role_id) AS rol
FROM users;
