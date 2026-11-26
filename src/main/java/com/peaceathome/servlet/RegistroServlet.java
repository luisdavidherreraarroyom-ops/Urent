
package com.peaceathome.servlet;

import com.peaceathome.data.UniversidadDAO;
import com.peaceathome.data.UsuarioDAO;
import com.peaceathome.model.Universidad;
import com.peaceathome.model.Usuario;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private static final Pattern FORMATO_CORREO =
        Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String tipoCuenta = request.getParameter("tipoCuenta");
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String contrasena = request.getParameter("contrasena");
        String confirmarContrasena =
            request.getParameter("confirmarContrasena");

        String universidadParam =
            request.getParameter("universidad");

        String carrera = request.getParameter("carrera");
        String semestreParam = request.getParameter("semestre");

        if (tipoCuenta != null) tipoCuenta = tipoCuenta.trim();
        if (nombre != null) nombre = nombre.trim();
        if (correo != null) correo = correo.trim();
        if (telefono != null) telefono = telefono.trim();
        if (carrera != null) carrera = carrera.trim();

        if (vacio(nombre) || vacio(correo)
                || vacio(telefono) || vacio(contrasena)) {

            error(response, "campos_vacios");
            return;
        }

        String rol;

        if ("estudiante".equals(tipoCuenta)) {
            rol = "estudiante";
        } else if ("vendedor".equals(tipoCuenta)) {
            rol = "vendedor";
        } else {
            error(response, "tipo_invalido");
            return;
        }

        if (!FORMATO_CORREO.matcher(correo).matches()) {
            error(response, "correo_invalido");
            return;
        }

        if (nombre.length() > 100
                || correo.length() > 100
                || telefono.length() > 20) {

            error(response, "datos_invalidos");
            return;
        }

        if (contrasena.length() < 6) {
            error(response, "contrasena_corta");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            error(response, "contrasenas_no_coinciden");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if (usuarioDAO.existeCorreo(correo)) {
            error(response, "correo_existente");
            return;
        }

        Usuario nuevoUsuario = new Usuario();

        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setRol(rol);

        if ("estudiante".equals(rol)) {

            if (vacio(universidadParam)
                    || vacio(carrera)
                    || vacio(semestreParam)) {

                error(response, "campos_vacios");
                return;
            }

            int idUniversidad;
            int semestre;

            try {
                idUniversidad =
                    Integer.parseInt(universidadParam.trim());
            } catch (NumberFormatException e) {
                error(response, "universidad_invalida");
                return;
            }

            try {
                semestre =
                    Integer.parseInt(semestreParam.trim());
            } catch (NumberFormatException e) {
                error(response, "semestre_invalido");
                return;
            }

            if (semestre < 1 || semestre > 12) {
                error(response, "semestre_invalido");
                return;
            }

            if (carrera.length() > 100) {
                error(response, "datos_invalidos");
                return;
            }

            Universidad universidad =
                new UniversidadDAO().buscarActivaPorId(idUniversidad);

            if (universidad == null) {
                error(response, "universidad_invalida");
                return;
            }

            String dominioCorreo =
                correo.substring(correo.lastIndexOf('@') + 1)
                    .toLowerCase(Locale.ROOT);

            String dominioUniversidad =
                universidad.getDominioCorreo()
                    .toLowerCase(Locale.ROOT);

            boolean coincide =
                dominioCorreo.equals(dominioUniversidad)
                || dominioCorreo.endsWith("." + dominioUniversidad);

            if (!coincide) {
                error(response, "correo_no_institucional");
                return;
            }

            nuevoUsuario.setIdUniversidad(idUniversidad);
            nuevoUsuario.setCarrera(carrera);
            nuevoUsuario.setSemestre(semestre);
        }

        boolean registroExitoso =
            usuarioDAO.registrarUsuario(nuevoUsuario);

        if (registroExitoso) {

            response.sendRedirect("index.html?registro=exitoso");

        } else {

            error(response, "error_servidor");
        }
    }

    private static boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private static void error(HttpServletResponse response,
                              String codigo) throws IOException {

        response.sendRedirect("registro.html?error=" + codigo);
    }
}