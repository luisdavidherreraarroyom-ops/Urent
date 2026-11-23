-- =====================================================================
-- 03_perfil_roles_universidades.sql
-- Urent: roles de usuario, catalogo de universidades y perfil de estudiante
-- Se puede ejecutar varias veces sin errores. Ejecutar DESPUES de 01 y 02.
-- =====================================================================
USE PeaceAtHome;
GO

-- 1. Catalogo de universidades (el dominio del correo valida el registro)
IF OBJECT_ID('Universidad', 'U') IS NULL
CREATE TABLE Universidad (
    id_universidad INT IDENTITY(1,1) PRIMARY KEY,
    nombre         VARCHAR(150) NOT NULL UNIQUE,
    ciudad         VARCHAR(80),
    dominio_correo VARCHAR(100) NOT NULL UNIQUE,
    latitud        DECIMAL(9,6) NULL,
    longitud       DECIMAL(9,6) NULL,
    activa         BIT NOT NULL DEFAULT 1
);
GO

-- 2. Normalizar los roles que ya existen en Usuario
--    (quien tenga fila en Agente pasa a vendedor; el resto, a estudiante)
UPDATE Usuario SET rol = 'vendedor'
WHERE id_usuario IN (SELECT id_usuario FROM Agente);

UPDATE Usuario SET rol = 'estudiante'
WHERE LOWER(rol) NOT IN ('estudiante', 'vendedor', 'administrador');

UPDATE Usuario SET rol = LOWER(rol);
GO

-- 3. Valor por defecto y restriccion para el rol
IF NOT EXISTS (SELECT 1 FROM sys.default_constraints WHERE name = 'DF_Usuario_rol')
    ALTER TABLE Usuario ADD CONSTRAINT DF_Usuario_rol DEFAULT 'estudiante' FOR rol;

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_Usuario_rol')
    ALTER TABLE Usuario ADD CONSTRAINT CK_Usuario_rol
        CHECK (rol IN ('estudiante', 'vendedor', 'administrador'));
GO

-- 4. Perfil de estudiante en Usuario (opcional para vendedor y administrador)
IF COL_LENGTH('Usuario', 'id_universidad') IS NULL
    ALTER TABLE Usuario ADD id_universidad INT NULL;
IF COL_LENGTH('Usuario', 'carrera') IS NULL
    ALTER TABLE Usuario ADD carrera VARCHAR(100) NULL;
IF COL_LENGTH('Usuario', 'semestre') IS NULL
    ALTER TABLE Usuario ADD semestre TINYINT NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Usuario_Universidad')
    ALTER TABLE Usuario ADD CONSTRAINT FK_Usuario_Universidad
        FOREIGN KEY (id_universidad) REFERENCES Universidad(id_universidad);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_Usuario_semestre')
    ALTER TABLE Usuario ADD CONSTRAINT CK_Usuario_semestre
        CHECK (semestre IS NULL OR semestre BETWEEN 1 AND 12);
GO

-- 5. Universidades iniciales (VERIFICAR los dominios antes de usarlos)
INSERT INTO Universidad (nombre, ciudad, dominio_correo)
SELECT v.nombre, v.ciudad, v.dominio
FROM (VALUES
    ('Universidad de Antioquia',            'Medellin', 'udea.edu.co'),
    ('Universidad Nacional de Colombia',    'Medellin', 'unal.edu.co'),
    ('Universidad EAFIT',                   'Medellin', 'eafit.edu.co'),
    ('Universidad Pontificia Bolivariana',  'Medellin', 'upb.edu.co'),
    ('SENA',                                'Medellin', 'misena.edu.co')
) AS v(nombre, ciudad, dominio)
WHERE NOT EXISTS (SELECT 1 FROM Universidad u WHERE u.dominio_correo = v.dominio);
GO

-- 6. Primer administrador (NO se crea desde el registro):
--    1) Registra una cuenta normal desde la pagina.
--    2) Descomenta y ejecuta esto con su correo:
-- UPDATE Usuario SET rol = 'administrador' WHERE correo = 'admin@correo.com';

-- 7. Verificacion
SELECT rol, COUNT(*) AS usuarios FROM Usuario GROUP BY rol;
SELECT id_universidad, nombre, dominio_correo FROM Universidad;
GO
