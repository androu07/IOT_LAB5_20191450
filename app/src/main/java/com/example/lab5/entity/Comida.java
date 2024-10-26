package com.example.lab5.entity;

import java.io.Serializable;
import java.util.Date;

public class Comida implements Serializable {

    private String nombre;
    private int calorias;
    private String hora;
    private boolean esComida;

    public Comida(String nombre, int calorias, String hora, boolean esComida) {
        this.nombre = nombre;
        this.calorias = calorias;
        this.hora = hora;
        this.esComida = esComida;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCalorias() {
        return calorias;
    }

    public String getHora() {
        return hora;
    }

    public boolean isEsComida() {
        return esComida;
    }

}
