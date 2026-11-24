package com.peaceathome.filtro;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Protege las páginas que requieren sesión.
 * - Sin sesión: manda al login con el aviso «sesion_expirada».
 * - Con sesión pero rol equivocado: manda a la página de inicio de su rol.
 * - El administrador puede entrar a todas las páginas protegidas.
 * Para proteger una página nueva, agrégala a urlPatterns y a rolRequerido().
 */
@WebFilter(urlPatterns = {"/dashboard.jsp", "/panel-vendedor.jsp", "/panel-admin.jsp"})
public class FiltroAcceso implements Filter {

    /** Página de inicio de cada rol (ruta relativa a la aplicación). */
    public static String inicioPorRol(String rol) {
        if ("estudiante".equals(rol)) return "dashboard.jsp";
        if ("vendedor".equals(rol)) return "panel-vendedor.jsp";
        if ("administrador".equals(rol)) return "panel-admin.jsp";
        return "index.html?error=sin_permiso";
    }

    /** Rol que exige cada página protegida. */
    private static String rolRequerido(String ruta) {
        if (ruta.endsWith("/panel-vendedor.jsp")) return "vendedor";
        if (ruta.endsWith("/panel-admin.jsp")) return "administrador";
        return "estudiante"; // dashboard.jsp
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Evita que el botón «Atrás» muestre páginas protegidas después de cerrar sesión
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        String rol = (session == null) ? null : (String) session.getAttribute("rol");

        if (rol == null) {
            response.sendRedirect(request.getContextPath() + "/index.html?error=sesion_expirada");
            return;
        }

        if ("administrador".equals(rol) || rol.equals(rolRequerido(request.getServletPath()))) {
            chain.doFilter(req, res);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/" + inicioPorRol(rol));
    }
}