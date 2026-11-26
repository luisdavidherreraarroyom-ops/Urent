-- =====================================================================
-- 03b_primer_administrador.sql
-- Urent: convierte una cuenta YA REGISTRADA en administrador.
-- El administrador NO se puede crear desde el formulario de registro.
--
-- Pasos:
--   1) Registra la cuenta desde registro.html (con un correo ficticio).
--   2) Cambia el correo de la linea DECLARE por el de esa cuenta.
--   3) Ejecuta este script en SSMS (se puede ejecutar varias veces).
-- Ejecutar DESPUES de 03_perfil_roles_universidades.sql
-- =====================================================================
USE PeaceAtHome;
GO

DECLARE @correo VARCHAR(100) = 'David123@misena.edu.co';   -- <-- CAMBIA ESTE CORREO

IF NOT EXISTS (SELECT 1 FROM Usuario WHERE correo = @correo)
BEGIN
    PRINT 'No existe una cuenta con ese correo. Registrala primero desde registro.html.';
END
ELSE IF EXISTS (SELECT 1 FROM Usuario WHERE correo = @correo AND rol = 'administrador')
BEGIN
    PRINT 'Esa cuenta ya es administrador. No se hizo ningun cambio.';
END
ELSE
BEGIN
    UPDATE Usuario SET rol = 'administrador' WHERE correo = @correo;
    PRINT 'Listo: la cuenta ahora es administrador.';
END

-- Verificacion: administradores actuales
SELECT id_usuario, correo, rol FROM Usuario WHERE rol = 'administrador';
GO
