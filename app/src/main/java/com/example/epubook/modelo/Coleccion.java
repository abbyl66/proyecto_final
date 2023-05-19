package com.example.epubook.modelo;

public class Coleccion {

    private String nombre;
    private boolean seleccionado;

    public Coleccion() {
    }

    public Coleccion(String coleccion, boolean seleccionado) {
        this.nombre = coleccion;
        this.seleccionado = seleccionado;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String coleccion) {
        this.nombre = coleccion;
    }

}
