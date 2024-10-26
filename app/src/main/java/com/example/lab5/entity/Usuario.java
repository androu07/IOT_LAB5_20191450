package com.example.lab5.entity;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String peso;
    private String altura;
    private String edad;
    private String genero;
    private String gastoCalorico;
    private String consumoCaloricoDiario;

    public Usuario(String peso, String altura, String edad, String genero, String gastoCalorico, String consumoCaloricoDiario) {
        this.peso = peso;
        this.altura = altura;
        this.edad = edad;
        this.genero = genero;
        this.gastoCalorico = gastoCalorico;
        this.consumoCaloricoDiario = consumoCaloricoDiario;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getGastoCalorico() {
        return gastoCalorico;
    }

    public void setGastoCalorico(String gastoCalorico) {
        this.gastoCalorico = gastoCalorico;
    }

    public String getConsumoCaloricoDiario() {
        return consumoCaloricoDiario;
    }

    public void setConsumoCaloricoDiario(String consumoCaloricoDiario) {
        this.consumoCaloricoDiario = consumoCaloricoDiario;
    }
}
