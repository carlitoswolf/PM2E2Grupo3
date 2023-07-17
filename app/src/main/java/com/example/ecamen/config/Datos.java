package com.example.ecamen.Config;

public class Datos {
    private String id;
    private String nombres;
    private String telefono;
    private String latitud;
    private String longitud;
    private String firma;

    public Datos(String id, String nombres, String telefono, String latitud, String longitud, String firma) {
        this.id = id;
        this.nombres = nombres;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.firma = firma;
    }

    public Datos() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }
}
