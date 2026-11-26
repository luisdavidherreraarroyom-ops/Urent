package com.peaceathome.model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private String telefono;

    // Perfil de estudiante (pueden ser null para vendedor y administrador)
    private Integer idUniversidad;
    private String carrera;
    private Integer semestre;

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String correo, String contrasena, String rol, String telefono) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.telefono = telefono;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getIdUniversidad() { return idUniversidad; }
    public void setIdUniversidad(Integer idUniversidad) { this.idUniversidad = idUniversidad; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
}