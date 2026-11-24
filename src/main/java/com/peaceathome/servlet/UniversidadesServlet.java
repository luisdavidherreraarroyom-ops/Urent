package com.peaceathome.servlet;

import com.peaceathome.data.UniversidadDAO;
import com.peaceathome.model.Universidad;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Entrega la lista de universidades en JSON para el selector del registro. */
@WebServlet("/universidades")
public class UniversidadesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Universidad> lista = new UniversidadDAO().listarActivas();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Universidad u = lista.get(i);
            if (i > 0) json.append(",");
            json.append("{\"id\":").append(u.getIdUniversidad())
                .append(",\"nombre\":\"").append(escapar(u.getNombre()))
                .append("\",\"dominio\":\"").append(escapar(u.getDominioCorreo()))
                .append("\"}");
        }
        json.append("]");

        PrintWriter out = response.getWriter();
        out.print(json);
    }

    private static String escapar(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}