package com.example.epubook.modelo;

import android.net.Uri;

import java.util.Date;

public class ArchivoEpub {

    String nombre, uri;
    Double tamanio;
    Date fecha;

    public ArchivoEpub() {
    }

    public ArchivoEpub(String nombre, String uri, Double tamanio, Date fecha) {
        this.nombre = nombre;
        this.uri = uri;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
