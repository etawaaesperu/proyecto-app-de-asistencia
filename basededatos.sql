-- ============================================================
--  BASE DE DATOS: asistencia_db
--  Ejecutar en MySQL antes de levantar el backend
-- ============================================================

CREATE DATABASE IF NOT EXISTS asistencia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE asistencia_db;

-- ------------------------------------------------------------
-- TABLAS  (Hibernate las crea automáticamente con ddl-auto=update
--          pero este script las deja listas con datos de prueba)
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS empleados (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo           VARCHAR(20),
    nombre           VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    departamento     VARCHAR(100),
    cargo            VARCHAR(100),
    dni              VARCHAR(20)  NOT NULL,
    email            VARCHAR(150),
    telefono         VARCHAR(20),
    fecha_nacimiento VARCHAR(20),
    estado_civil     VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario     VARCHAR(80)  NOT NULL UNIQUE,
    contrasena  VARCHAR(100) NOT NULL,
    rol         VARCHAR(20)  NOT NULL,
    empleado_id BIGINT,
    FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

CREATE TABLE IF NOT EXISTS horarios (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id  BIGINT NOT NULL,
    fecha        VARCHAR(20),
    hora_entrada VARCHAR(10),
    hora_salida  VARCHAR(10),
    observaciones TEXT,
    FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

CREATE TABLE IF NOT EXISTS asistencias (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id   BIGINT NOT NULL,
    fecha         VARCHAR(20),
    hora_entrada  VARCHAR(10),
    hora_salida   VARCHAR(10),
    estado        VARCHAR(20),
    justificacion TEXT,
    FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

-- ------------------------------------------------------------
-- DATOS DE PRUEBA
-- ------------------------------------------------------------

-- Empleado gerente
INSERT INTO empleados (codigo, nombre, apellidos, departamento, cargo, dni, email)
VALUES ('EMP-001', 'Admin', 'Sistema', 'Administración', 'Gerente General', '00000001', 'admin@empresa.com');

-- Empleado regular
INSERT INTO empleados (codigo, nombre, apellidos, departamento, cargo, dni, email)
VALUES ('EMP-002', 'Juan', 'Pérez López', 'Recursos Humanos', 'Asistente', '12345678', 'juan.perez@empresa.com');

-- ============================================================
--  CREDENCIALES DE ACCESO
-- ============================================================
--  ROL GERENTE  →  usuario: gerente01   |  contraseña: 123456
--  ROL EMPLEADO →  usuario: empleado01  |  contraseña: 123456
-- ============================================================

INSERT INTO usuarios (usuario, contrasena, rol, empleado_id)
VALUES ('gerente01', '123456', 'gerente', 1);

INSERT INTO usuarios (usuario, contrasena, rol, empleado_id)
VALUES ('empleado01', '123456', 'empleado', 2);
