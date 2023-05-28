package com.example.epubook.modelo;

public class LibroEscribir {
    String titulo, autor, portada;

    public LibroEscribir() {
    }

    public LibroEscribir(String titulo, String autor, String portada) {
        this.titulo = titulo;
        this.autor = autor;
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

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }
}
