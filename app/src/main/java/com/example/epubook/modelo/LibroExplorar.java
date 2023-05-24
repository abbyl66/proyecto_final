package com.example.epubook.modelo;

import android.graphics.Bitmap;

public class LibroExplorar {

    private String titulo;
    private String autor;
    private Bitmap portada;
    private String ruta;
    boolean guardado;
    boolean descargando;

    public LibroExplorar() {
    }

    public LibroExplorar(String titulo, String autor, Bitmap portada, String ruta, boolean guardado, boolean descargando) {
        this.titulo = titulo;
        this.autor = autor;
        this.portada = portada;
        this.ruta = ruta;
        this.guardado = guardado;
        this.descargando = descargando;
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

    public Bitmap getPortada() {
        return portada;
    }

    public void setPortada(Bitmap portada) {
        this.portada = portada;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

    public boolean isDescargando() {
        return descargando;
    }

    public void setDescargando(boolean descargando) {
        this.descargando = descargando;
    }
}
