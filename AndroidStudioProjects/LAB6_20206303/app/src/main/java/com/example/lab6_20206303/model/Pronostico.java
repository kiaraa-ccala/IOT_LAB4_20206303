package com.example.lab6_20206303.model;

import java.io.Serializable;

public class Pronostico implements Serializable {
    private String id;
    private String seleccionA;
    private String seleccionB;
    private String fechaPartido;
    private int golesA;
    private int golesB;
    private String estado; // "Pendiente", "Acertado", "Fallado"
    private String userId;

    public Pronostico() {
        // Required for Firebase
    }

    public Pronostico(String id, String seleccionA, String seleccionB, String fechaPartido, int golesA, int golesB, String estado, String userId) {
        this.id = id;
        this.seleccionA = seleccionA;
        this.seleccionB = seleccionB;
        this.fechaPartido = fechaPartido;
        this.golesA = golesA;
        this.golesB = golesB;
        this.estado = estado;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSeleccionA() { return seleccionA; }
    public void setSeleccionA(String seleccionA) { this.seleccionA = seleccionA; }

    public String getSeleccionB() { return seleccionB; }
    public void setSeleccionB(String seleccionB) { this.seleccionB = seleccionB; }

    public String getFechaPartido() { return fechaPartido; }
    public void setFechaPartido(String fechaPartido) { this.fechaPartido = fechaPartido; }

    public int getGolesA() { return golesA; }
    public void setGolesA(int golesA) { this.golesA = golesA; }

    public int getGolesB() { return golesB; }
    public void setGolesB(int golesB) { this.golesB = golesB; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
