package com.example.epubook.modelo;

public class Usuario {

    private String nombre, email, user, ctrsenia, fotoPerfil;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String user, String ctrsenia, String fotoPerfil) {
        this.nombre = nombre;
        this.email = email;
        this.user = user;
        this.ctrsenia = ctrsenia;
        this.fotoPerfil = fotoPerfil;
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
