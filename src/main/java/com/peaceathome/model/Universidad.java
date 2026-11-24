package com.peaceathome.model;

public class Universidad {
    private int idUniversidad;
    private String nombre;
    private String ciudad;
    private String dominioCorreo;

    public Universidad() {}

    public Universidad(int idUniversidad, String nombre, String ciudad, String dominioCorreo) {
        this.idUniversidad = idUniversidad;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.dominioCorreo = dominioCorreo;
    }

    public int getIdUniversidad() { return idUniversidad; }
    public void setIdUniversidad(int idUniversidad) { this.idUniversidad = idUniversidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDominioCorreo() { return dominioCorreo; }
    public void setDominioCorreo(String dominioCorreo) { this.dominioCorreo = dominioCorreo; }
}