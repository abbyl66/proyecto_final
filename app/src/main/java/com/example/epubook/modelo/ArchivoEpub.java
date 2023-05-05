package com.example.epubook.modelo;

import java.util.Date;

public class ArchivoEpub {

    String nombre;
    Double tamanio;
    Date fecha;

    public ArchivoEpub() {
    }

    public ArchivoEpub(String nombre, Double tamanio, Date fecha) {
        this.nombre = nombre;
        this.tamanio = tamanio;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getTamanio() {
        return tamanio;
    }

    public void setTamanio(Double tamanio) {
        this.tamanio = tamanio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date   fecha) {
        this.fecha = fecha;
    }
}
