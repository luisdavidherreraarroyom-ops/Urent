package com.peaceathome.data;

import com.peaceathome.conexion.Conexion;
import com.peaceathome.model.Universidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UniversidadDAO {

    /** Universidades activas, ordenadas por nombre (para el selector del registro). */
    public List<Universidad> listarActivas() {
        List<Universidad> lista = new ArrayList<>();
        String sql = "SELECT id_universidad, nombre, ciudad, dominio_correo "
                   + "FROM dbo.Universidad WHERE activa = 1 ORDER BY nombre";

        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Universidad(
                            rs.getInt("id_universidad"),
                            rs.getString("nombre"),
                            rs.getString("ciudad"),
                            rs.getString("dominio_correo")));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar las universidades.");
            e.printStackTrace();
        }

        return lista;
    }

    /** Busca una universidad activa por su id; devuelve null si no existe o está inactiva. */
    public Universidad buscarActivaPorId(int id) {
        String sql = "SELECT id_universidad, nombre, ciudad, dominio_correo "
                   + "FROM dbo.Universidad WHERE id_universidad = ? AND activa = 1";

        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Universidad(
                                rs.getInt("id_universidad"),
                                rs.getString("nombre"),
                                rs.getString("ciudad"),
                                rs.getString("dominio_correo"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar la universidad.");
            e.printStackTrace();
        }

        return null;
    }
}