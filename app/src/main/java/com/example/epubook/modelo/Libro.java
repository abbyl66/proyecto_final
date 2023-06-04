package com.example.epubook.modelo;

import android.graphics.Bitmap;

public class Libro {

    private String titulo;
    private String autor;
    private Bitmap portada;
    private String ruta;
    private int marcX, marcY;

    public Libro() {
    }

    public Libro(String titulo, String autor, Bitmap portada, String ruta, int marcX, int marcY) {
        this.titulo = titulo;
        this.autor = autor;
        this.portada = portada;
        this.ruta = ruta;
        this.marcX = marcX;
        this.marcY = marcY;
    }

    public int getMarcX() {
        return marcX;
    }

    public void setMarcX(int marcX) {
        this.marcX = marcX;
    }

    public int getMarcY() {
        return marcY;
    }

    public void setMarcY(int marcY) {
        this.marcY = marcY;
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
