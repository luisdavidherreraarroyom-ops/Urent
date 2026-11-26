
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
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null) {
            email = email.trim();
        }

        if (password != null) {
            password = password.trim();
        }

        if (email == null || email.isEmpty()
                || password == null || password.isEmpty()) {

            response.sendRedirect("index.html?error=campos_vacios");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.autenticar(email, password);

        if (usuario != null) {

            HttpSession anterior = request.getSession(false);

            if (anterior != null) {
                anterior.invalidate();
            }

            HttpSession session = request.getSession(true);

            session.setMaxInactiveInterval(MINUTOS_SESION * 60);

            session.setAttribute("usuario", usuario.getCorreo());
            session.setAttribute("usuarioId", usuario.getIdUsuario());
            session.setAttribute("nombre", usuario.getNombre());
            session.setAttribute("rol", usuario.getRol());

            response.sendRedirect(
                FiltroAcceso.inicioPorRol(usuario.getRol())
            );

        } else {

            response.sendRedirect(
                "index.html?error=invalid_credentials"
            );
        }
    }
}
