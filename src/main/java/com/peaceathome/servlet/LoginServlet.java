package com.peaceathome.servlet;

import com.peaceathome.data.UsuarioDAO;
import com.peaceathome.filtro.FiltroAcceso;
import com.peaceathome.model.Usuario;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final int MINUTOS_SESION = 30;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Limpiamos espacios accidentales que envíe el formulario
        if (email != null) email = email.trim();
        if (password != null) password = password.trim();

        System.out.println("=== INTENTO DE LOGIN ===");
        System.out.println("Email recibido: [" + email + "]");

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.autenticar(email, password);

        if (usuario != null) {
            System.out.println("-> USUARIO ENCONTRADO Y CONTRASEÑA CORRECTA. Rol: " + usuario.getRol());

            // Cerramos cualquier sesión anterior y creamos una nueva
            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(MINUTOS_SESION * 60);

            session.setAttribute("usuario", usuario.getCorreo()); // lo lee dashboard.jsp
            session.setAttribute("usuarioId", usuario.getIdUsuario());
            session.setAttribute("nombre", usuario.getNombre());
            session.setAttribute("rol", usuario.getRol());

            response.sendRedirect(FiltroAcceso.inicioPorRol(usuario.getRol()));
        } else {
            System.out.println("-> CREDENCIALES INCORRECTAS");
            response.sendRedirect("index.html?error=invalid_credentials");
        }
    }
}