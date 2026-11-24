package com.peaceathome.data;

import com.peaceathome.conexion.Conexion;
import com.peaceathome.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class UsuarioDAO {

    /**
     * Método para validar las credenciales del Login
     * @param correoIngresado El correo que el usuario digita en la vista
     * @param contrasenaIngresada La clave en texto plano que el usuario digita
     * @return true si las credenciales son correctas, false si no.
     */
    public boolean iniciarSesion(String correoIngresado, String contrasenaIngresada) {
        String sql = "SELECT contrasena FROM dbo.Usuario WHERE correo = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correoIngresado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashBaseDatos = rs.getString("contrasena");
                    return BCrypt.checkpw(contrasenaIngresada, hashBaseDatos);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar las credenciales en la base de datos.");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Valida las credenciales y devuelve el usuario completo (con su rol).
     * @param correo El correo que el usuario digita
     * @param contrasenaIngresada La clave en texto plano que el usuario digita
     * @return el Usuario (sin la contraseña) si las credenciales son correctas, o null si no
     */
    public Usuario autenticar(String correo, String contrasenaIngresada) {
        if (correo == null || correo.isEmpty()
                || contrasenaIngresada == null || contrasenaIngresada.isEmpty()) {
            return null;
        }

        String sql = "SELECT id_usuario, nombre, correo, contrasena, rol, telefono "
                   + "FROM dbo.Usuario WHERE correo = ?";

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                System.err.println("❌ No hay conexión a la base de datos (revisa las variables de entorno).");
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, correo);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hash = rs.getString("contrasena");

                        boolean coincide;
                        try {
                            coincide = BCrypt.checkpw(contrasenaIngresada, hash);
                        } catch (IllegalArgumentException e) {
                            // Contraseña guardada sin hash BCrypt válido
                            System.err.println("❌ El usuario tiene una contraseña sin formato BCrypt válido.");
                            return null;
                        }

                        if (coincide) {
                            Usuario u = new Usuario();
                            u.setIdUsuario(rs.getInt("id_usuario"));
                            u.setNombre(rs.getString("nombre"));
                            u.setCorreo(rs.getString("correo"));
                            u.setRol(rs.getString("rol"));
                            u.setTelefono(rs.getString("telefono"));
                            return u; // la contraseña NO se devuelve
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al autenticar al usuario.");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verifica si ya existe un usuario registrado con ese correo.
     * @param correo El correo a verificar
     * @return true si el correo ya está registrado, false si está libre
     */
    public boolean existeCorreo(String correo) {
        String sql = "SELECT id_usuario FROM dbo.Usuario WHERE correo = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true si encontró una fila, false si no
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de correo.");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Inserta un nuevo usuario con su perfil, hasheando la contraseña con BCrypt.
     * Si el rol es "vendedor", crea también su fila en Agente (todo en una transacción).
     * @param usuario Objeto Usuario con los datos ingresados (contrasena en texto plano)
     * @return true si el registro fue exitoso, false si hubo error
     */
    public boolean registrarUsuario(Usuario usuario) {
        String sqlUsuario = "INSERT INTO dbo.Usuario "
                + "(nombre, correo, contrasena, rol, telefono, id_universidad, carrera, semestre) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlAgente = "INSERT INTO dbo.Agente (id_usuario) VALUES (?)";

        // Nunca guardamos la contraseña en texto plano
        String hash = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt());

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                System.err.println("❌ No hay conexión a la base de datos (revisa las variables de entorno).");
                return false;
            }

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getCorreo());
                ps.setString(3, hash);
                ps.setString(4, usuario.getRol());
                ps.setString(5, usuario.getTelefono());

                if (usuario.getIdUniversidad() != null) {
                    ps.setInt(6, usuario.getIdUniversidad());
                } else {
                    ps.setNull(6, Types.INTEGER);
                }
                if (usuario.getCarrera() != null) {
                    ps.setString(7, usuario.getCarrera());
                } else {
                    ps.setNull(7, Types.VARCHAR);
                }
                if (usuario.getSemestre() != null) {
                    ps.setInt(8, usuario.getSemestre());
                } else {
                    ps.setNull(8, Types.TINYINT);
                }

                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                int idNuevo = 0;
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    if (claves.next()) {
                        idNuevo = claves.getInt(1);
                    }
                }

                // El vendedor necesita una fila en Agente para poder publicar propiedades
                if ("vendedor".equals(usuario.getRol())) {
                    if (idNuevo == 0) {
                        con.rollback();
                        return false;
                    }
                    try (PreparedStatement psAgente = con.prepareStatement(sqlAgente)) {
                        psAgente.setInt(1, idNuevo);
                        psAgente.executeUpdate();
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar el usuario en la base de datos.");
            e.printStackTrace();
        }

        return false;
    }
}