package com.peaceathome.servlet;

import com.peaceathome.data.UsuarioDAO;
import com.peaceathome.model.Usuario;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String contrasena = request.getParameter("contrasena");
        String confirmarContrasena = request.getParameter("confirmarContrasena");

        // Limpieza básica de espacios
        if (nombre != null) nombre = nombre.trim();
        if (correo != null) correo = correo.trim();
        if (telefono != null) telefono = telefono.trim();

        System.out.println("=== INTENTO DE REGISTRO ===");
        System.out.println("Correo recibido: [" + correo + "]");

        // Validación 1: campos vacíos
        if (nombre == null || nombre.isEmpty() ||
            correo == null || correo.isEmpty() ||
            telefono == null || telefono.isEmpty() ||
            contrasena == null || contrasena.isEmpty()) {

            response.sendRedirect("registro.html?error=campos_vacios");
            return;
        }

        // Validación 2: las contraseñas coinciden
        if (!contrasena.equals(confirmarContrasena)) {
            response.sendRedirect("registro.html?error=contrasenas_no_coinciden");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Validación 3: correo no repetido
        if (usuarioDAO.existeCorreo(correo)) {
            System.out.println("-> CORREO YA REGISTRADO");
            response.sendRedirect("registro.html?error=correo_existente");
            return;
        }

        // Armamos el objeto Usuario (rol fijo, no lo elige el usuario)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena); // el DAO se encarga de hashearla
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setRol("estudiante");

        boolean registroExitoso = usuarioDAO.registrarUsuario(nuevoUsuario);

        if (registroExitoso) {
            System.out.println("-> USUARIO REGISTRADO CORRECTAMENTE");
            response.sendRedirect("index.html?registro=exitoso");
        } else {
            System.out.println("-> ERROR AL REGISTRAR USUARIO");
            response.sendRedirect("registro.html?error=error_servidor");
        }
    }
}