<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Bienvenido - Urent</title>
    <style>
        body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; }
        .card { display: inline-block; padding: 20px 40px; border: 1px solid #ddd; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        a { color: #d9534f; text-decoration: none; font-weight: bold; }
    </style>
</head>
<body>
    <div class="card">
        <h1>¡Bienvenido a Urent!</h1>
        <p>Has iniciado sesión exitosamente con el correo:</p>
        <h3><%= session.getAttribute("usuarioLogueado") %></h3>
        <br>
        <a href="index.html">Cerrar Sesión</a>
    </div>
</body>
</html>