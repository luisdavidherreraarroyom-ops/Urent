<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.peaceathome.data.PropiedadDAO" %>
<%@ page import="com.peaceathome.model.Propiedad" %>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("index.html");
        return;
    }

    PropiedadDAO propiedadDAO = new PropiedadDAO();
    int totalPropiedades = propiedadDAO.contarTotal();

    // Leer filtros de la URL
    String filtroTipo = request.getParameter("tipo");
    String filtroEstado = request.getParameter("estado");
    String filtroPrecioMin = request.getParameter("precioMin");
    String filtroPrecioMax = request.getParameter("precioMax");

    BigDecimal precioMin = null;
    BigDecimal precioMax = null;
    try {
        if (filtroPrecioMin != null && !filtroPrecioMin.isEmpty()) {
            precioMin = new BigDecimal(filtroPrecioMin);
        }
        if (filtroPrecioMax != null && !filtroPrecioMax.isEmpty()) {
            precioMax = new BigDecimal(filtroPrecioMax);
        }
    } catch (NumberFormatException e) {
        // Si el usuario mete algo raro en el precio, ignoramos ese filtro
        precioMin = null;
        precioMax = null;
    }

    boolean hayFiltros = (filtroTipo != null && !filtroTipo.isEmpty())
                       || (filtroEstado != null && !filtroEstado.isEmpty())
                       || precioMin != null
                       || precioMax != null;

    List<Propiedad> listaPropiedades = hayFiltros
        ? propiedadDAO.buscarConFiltros(filtroTipo, precioMin, precioMax, filtroEstado)
        : propiedadDAO.listarTodas();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Urent - Panel Principal</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif; }
        body { background-color: #f4f7f6; color: #333; min-height: 100vh; }
        .navbar { background-color: #ffffff; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08); }
        .navbar h1 { color: #2c3e50; font-size: 1.4rem; font-family: "Georgia", "Times New Roman", serif; }
        .user-nav { display: flex; align-items: center; gap: 15px; }
        .user-email { color: #007bff; font-weight: 600; }
        .btn-logout { padding: 0.5rem 1rem; background-color: #dc3545; color: white; text-decoration: none; border-radius: 6px; font-size: 0.9rem; font-weight: bold; }
        .btn-logout:hover { background-color: #bd2130; }
        .container { max-width: 1100px; margin: 2.5rem auto; padding: 0 1.5rem; }
        .welcome-card { background: #ffffff; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05); margin-bottom: 2rem; }
        .welcome-card h2 { color: #2c3e50; margin-bottom: 0.5rem; }
        .welcome-card p { color: #666; line-height: 1.6; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; }
        .card { background: #ffffff; padding: 1.5rem; border-radius: 10px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05); border-left: 5px solid #007bff; }
        .card h3 { font-size: 1rem; color: #555; margin-bottom: 0.5rem; }
        .card .value { font-size: 1.8rem; font-weight: bold; color: #2c3e50; }
        .propiedades-section { background: #ffffff; padding: 1.5rem; border-radius: 10px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05); margin-top: 1.5rem; }
        .propiedades-section h3 { color: #2c3e50; margin-bottom: 1rem; }
        .filtros { display: flex; flex-wrap: wrap; gap: 1rem; margin-bottom: 1.5rem; align-items: flex-end; }
        .filtro-grupo { display: flex; flex-direction: column; gap: 0.4rem; }
        .filtro-grupo label { font-size: 0.8rem; color: #666; }
        .filtro-grupo select, .filtro-grupo input { padding: 0.5rem; border: 1px solid #ccc; border-radius: 6px; font-size: 0.9rem; min-width: 140px; }
        .btn-filtrar { padding: 0.6rem 1.2rem; background-color: #007bff; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer; }
        .btn-filtrar:hover { background-color: #0056b3; }
        .btn-limpiar { padding: 0.6rem 1.2rem; background-color: #eee; color: #555; border: none; border-radius: 6px; font-weight: 600; cursor: pointer; text-decoration: none; display: inline-block; }
        .btn-limpiar:hover { background-color: #ddd; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #eee; }
        th { color: #888; font-size: 0.85rem; text-transform: uppercase; }
        td { color: #333; }
        .badge { padding: 0.25rem 0.6rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }
        .badge-disponible { background-color: #e8f5e9; color: #1b5e20; }
        .badge-vendido, .badge-arrendado { background-color: #fdecea; color: #b71c1c; }
        .sin-propiedades { text-align: center; color: #888; padding: 2rem; }
    </style>
</head>
<body>

    <header class="navbar">
        <h1>Panel Principal</h1>
        <div class="user-nav">
            <span class="user-email"><%= usuario %></span>
            <a href="logout" class="btn-logout">Cerrar Sesión</a>
        </div>
    </header>

    <main class="container">
        <section class="welcome-card">
            <h2>¡Bienvenido a Urent!</h2>
            <p>Has iniciado sesión correctamente como <strong><%= usuario %></strong> en el sistema de gestión <strong>Urent</strong>.</p>
        </section>

        <section class="grid">
            <div class="card">
                <h3>Propiedades Registradas</h3>
                <div class="value"><%= totalPropiedades %></div>
            </div>
            <div class="card" style="border-left-color: #28a745;">
                <h3>Solicitudes Activas</h3>
                <div class="value">0</div>
            </div>
            <div class="card" style="border-left-color: #ffc107;">
                <h3>Pagos Pendientes</h3>
                <div class="value">$0</div>
            </div>
        </section>

        <section class="propiedades-section">
            <h3>Propiedades disponibles</h3>

            <form method="get" action="dashboard.jsp" class="filtros">
                <div class="filtro-grupo">
                    <label for="tipo">Tipo</label>
                    <select id="tipo" name="tipo">
                        <option value="">Todos</option>
                        <option value="Casa" <%= "Casa".equals(filtroTipo) ? "selected" : "" %>>Casa</option>
                        <option value="Apartamento" <%= "Apartamento".equals(filtroTipo) ? "selected" : "" %>>Apartamento</option>
                        <option value="Apartaestudio" <%= "Apartaestudio".equals(filtroTipo) ? "selected" : "" %>>Apartaestudio</option>
                        <option value="Local Comercial" <%= "Local Comercial".equals(filtroTipo) ? "selected" : "" %>>Local Comercial</option>
                    </select>
                </div>

                <div class="filtro-grupo">
                    <label for="estado">Estado</label>
                    <select id="estado" name="estado">
                        <option value="">Todos</option>
                        <option value="Disponible" <%= "Disponible".equals(filtroEstado) ? "selected" : "" %>>Disponible</option>
                        <option value="Arrendado" <%= "Arrendado".equals(filtroEstado) ? "selected" : "" %>>Arrendado</option>
                        <option value="Vendido" <%= "Vendido".equals(filtroEstado) ? "selected" : "" %>>Vendido</option>
                    </select>
                </div>

                <div class="filtro-grupo">
                    <label for="precioMin">Precio mínimo</label>
                    <input type="number" id="precioMin" name="precioMin" placeholder="Ej: 100000000" value="<%= filtroPrecioMin != null ? filtroPrecioMin : "" %>">
                </div>

                <div class="filtro-grupo">
                    <label for="precioMax">Precio máximo</label>
                    <input type="number" id="precioMax" name="precioMax" placeholder="Ej: 500000000" value="<%= filtroPrecioMax != null ? filtroPrecioMax : "" %>">
                </div>

                <button type="submit" class="btn-filtrar">Filtrar</button>
                <a href="dashboard.jsp" class="btn-limpiar">Limpiar</a>
            </form>

            <% if (listaPropiedades.isEmpty()) { %>
                <p class="sin-propiedades">No se encontraron propiedades con esos filtros.</p>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Dirección</th>
                            <th>Tipo</th>
                            <th>Precio</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Propiedad p : listaPropiedades) { %>
                            <tr>
                                <td><%= p.getDireccion() %></td>
                                <td><%= p.getTipo() %></td>
                                <td>$<%= p.getPrecio() %></td>
                                <td><span class="badge badge-<%= p.getEstado().toLowerCase() %>"><%= p.getEstado() %></span></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </section>
    </main>

</body>
</html>