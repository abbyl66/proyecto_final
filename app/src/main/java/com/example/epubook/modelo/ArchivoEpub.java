package com.example.epubook.modelo;

import android.net.Uri;

import java.util.Date;

public class ArchivoEpub {

    String nombre, uri;
    Double tamanio;
    Date fecha;
    boolean guardado;
    boolean descargando;

    public ArchivoEpub() {
    }

    public ArchivoEpub(String nombre, String uri, Double tamanio, Date fecha, Boolean guardado, Boolean descargando) {
        this.nombre = nombre;
        this.uri = uri;
        this.tamanio = tamanio;
        this.fecha = fecha;
        this.guardado = guardado;
        this.descargando = descargando;
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

