package com.example.epubook.modelo;

import android.graphics.Bitmap;

public class Libro {

    private String titulo;
    private String autor;
    private Bitmap portada;
    private String ruta;
    boolean guardado;

    public Libro() {
    }

    public Libro(String titulo, String autor, Bitmap portada, String ruta, boolean guardado) {
        this.titulo = titulo;
        this.autor = autor;
        this.portada = portada;
        this.ruta = ruta;
        this.guardado = guardado;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Bitmap getPortada() {
        return portada;
    }

    public void setPortada(Bitmap portada) {
        this.portada = portada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

}
