package com.peaceathome.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unica clase de conexion a la base de datos de Urent.
 *
 * Los datos de acceso NO van en el codigo. Se leen de variables de entorno:
 *   URENT_DB_USER       (obligatoria) usuario de SQL Server
 *   URENT_DB_PASSWORD   (obligatoria) contrasena de ese usuario
 *   URENT_DB_HOST       (opcional)    por defecto: localhost
 *   URENT_DB_INSTANCE   (opcional)    por defecto: SQLEXPRESS
 *   URENT_DB_NAME       (opcional)    por defecto: PeaceAtHome
 */
public class Conexion {

    private static String variable(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        return valor.trim();
    }

    private static String url() {
        return "jdbc:sqlserver://" + variable("URENT_DB_HOST", "localhost")
                + ";instanceName=" + variable("URENT_DB_INSTANCE", "SQLEXPRESS") + ";"
                + "databaseName=" + variable("URENT_DB_NAME", "PeaceAtHome") + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;";
    }

    public static Connection conectar() {
        Connection con = null;

        String usuario = variable("URENT_DB_USER", null);
        String clave = variable("URENT_DB_PASSWORD", null);

        if (usuario == null || clave == null) {
            System.out.println("Faltan las variables de entorno URENT_DB_USER y URENT_DB_PASSWORD");
            return null;
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(url(), usuario, clave);
            System.out.println("Conexion exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver de SQL Server no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }

        return con;
    }
}