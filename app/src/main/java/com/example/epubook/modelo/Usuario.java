package com.example.epubook.modelo;

import java.util.List;

public class Usuario {

    private String nombre, email, user, ctrsenia, fotoPerfil;
    private List<String> historial;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String user, String ctrsenia, String fotoPerfil, List<String> historial) {
        this.nombre = nombre;
        this.email = email;
        this.user = user;
        this.ctrsenia = ctrsenia;
        this.fotoPerfil = fotoPerfil;
        this.historial = historial;
    }

    public List<String> getHistorial() {
        return historial;
    }

    public void setHistorial(List<String> historial) {
        this.historial = historial;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCtrsenia() {
        return ctrsenia;
    }

    public void setCtrsenia(String ctrsenia) {
        this.ctrsenia = ctrsenia;
    }
}
